package zw.co.dobadoba.msgexchange.service.message;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.ratelimit.ConfigurationHolder;
import zw.co.dobadoba.msgexchange.service.ratelimit.SourceRateLimitBuilder;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSender;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;

import java.util.Map;


/**
 * Created by dobadoba on 7/8/17.
 */
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    private MessageRepository messageRepository;
    private RabbitTemplate rabbitTemplate;
    private MessageSender messageSender;
    private String queueName;
    @Value("${rest.destination.success.code}")
    private String successCode;
    private int queued=0;
    private int posted=0;
    private int messageCount=0;
    private RateLimiter sinkRateLimiter;
    private RateLimiter sourceRateLimiter;
    private Map<String,RateLimiter> sourceRateLimits;
    private Map<String,Integer> totalReceivedForSource;
    private int totalMessagesReceived=0;

    public MessageServiceImpl(MessageRepository messageRepository, RabbitTemplate rabbitTemplate, MessageSender messageSender, String queueName) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.messageSender = messageSender;
        this.queueName = queueName;
    }

    public void setSinkRateLimiter(RateLimiter sinkRateLimiter) {
        this.sinkRateLimiter = sinkRateLimiter;
    }

    public void setSourceRateLimiter(RateLimiter sourceRateLimiter) {
        this.sourceRateLimiter = sourceRateLimiter;
    }

    public void setSourceRateLimits(Map<String, RateLimiter> sourceRateLimits) {
        this.sourceRateLimits = sourceRateLimits;
    }

    public void setTotalReceivedForSource(Map<String, Integer> totalReceivedForSource) {
        this.totalReceivedForSource = totalReceivedForSource;
    }

    public void setTotalMessagesReceived(int totalMessagesReceived) {
        this.totalMessagesReceived = totalMessagesReceived;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    @Override
    public Message processOutboundMessage(Msg msg,MessageSource messageSource) {
        sinkRateLimiter=ConfigurationHolder.getSinkRateLimit();
        if(!sinkRateLimiter.tryAcquire() && messageSource.equals(MessageSource.DIRECT_FROM_SOURCE)){
            LOGGER.info("Cannot post message immediately: MessageCount for this second {} {} {}",messageCount,msg, "Message has been posted to queue  and will be send later");
            rabbitTemplate.convertAndSend(queueName,msg);
            queued++;
            return messageRepository.findByRef(msg.getRef());
        }
        if(!sinkRateLimiter.tryAcquire() && messageSource.equals(MessageSource.FROM_QUEUE)) {
            sinkRateLimiter.acquire();
        }
        ResponseEntity<Void> response = messageSender.postToDestination(msg);
        if(!successCode.equalsIgnoreCase(response.getStatusCode().toString())){
            LOGGER.info("Error ocucurred on posting message to destination: {} Response from destination: {}",msg, response);
            throw new MessageExchangeException("Could not post message to destination"+response);
        }
        if(++messageCount>=ConfigurationHolder.getConfig().getSinkRate()){
            messageCount=0;
        }
        posted++;
        LOGGER.info("Message Count for this Second: {} Total Posted: {} Posted Via Queue: {} Sink Rate: {}",messageCount,posted,queued,ConfigurationHolder.getConfig().getSinkRate());
        return updateMessage(msg);
    }

    @Override
    public Message processInboundMessage(Msg message) {
        validateMessage(message);
        final String source =  message.getSource();
        preProcess();
        sourceRateLimiter=sourceRateLimits.get(source);
        int sourceTotal=totalReceivedForSource.get(source);
        int sourceRate = ConfigurationHolder.getConfig().getConfig().get(source);
        totalMessagesReceived++;
        sourceTotal++;
        if(!sourceRateLimiter.tryAcquire()){
            LOGGER.info("#########################Ignoring Surplus Message############################\n Message: {} Configured Source Rate: {}" +
                    "Total Received from Source: {} Total Messages Received: {} ",message,sourceRate,sourceTotal,totalMessagesReceived);
            updateSourceMessageCounts(source,sourceTotal);
            return null;
        }
        LOGGER.info("****************Processing  Message*****************************\n Message: {} Configured Source Rate: {} " +
                "Total Received from Source: {} Total Messages Received: {} ",message,sourceRate,sourceTotal,totalMessagesReceived);
         updateSourceMessageCounts(source,sourceTotal);
         save(message);
        return processOutboundMessage(message,MessageSource.DIRECT_FROM_SOURCE);
    }

    @Override
    public Message save(Msg msg){
        return messageRepository.save(buildMessageEntity(msg));
    }
    private void updateSourceMessageCounts(final String source,final int sourceTotal){
        totalReceivedForSource.put(source,sourceTotal);
        SourceRateLimitBuilder.updateSourceTotal(totalReceivedForSource);
    }

    @Override
    public Message updateMessage(Msg message) {
        Message messageEntity = messageRepository.findByRef(message.getRef());
        if(messageEntity==null){
            throw new MessageExchangeException("Updating message in database failed: Message Not Found");
        }
        messageEntity.setStatus(Status.RECEIVED_AT_SINK);
        messageEntity=messageRepository.save(messageEntity);
        LOGGER.info("Request Received at Sink: {} ",messageEntity);
        return messageEntity;
    }

    private void preProcess(){
        totalReceivedForSource=SourceRateLimitBuilder.getSourceMessageTotals();
        sourceRateLimits=SourceRateLimitBuilder.getSourceRateLimits();
    }

    public final Message buildMessageEntity(Msg messageReceived){
        Message messageEntity = new Message();
        messageEntity.setSource(messageReceived.getSource());
        messageEntity.setPayload(messageReceived.getPayload());
        messageEntity.setRef(messageReceived.getRef());
        return messageEntity;
    }

    private void validateMessage(Msg msg){
        if(msg==null){
            LOGGER.error("Cannot process empty message");
            throw new MessageExchangeException("Processing Failed. Empty or Message");
        }
        if(StringUtils.isEmpty(msg.getSource()) || StringUtils.isEmpty(msg.getPayload()) || StringUtils.isEmpty(msg.getRef()) )
        {
            LOGGER.error("Failed to process message due to missing details: {}",msg);
            throw new MessageExchangeException("Processing Failed. Missing Message Details");
        }
    }

}
