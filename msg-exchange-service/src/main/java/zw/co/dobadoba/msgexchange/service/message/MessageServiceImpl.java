package zw.co.dobadoba.msgexchange.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.service.amqp.QueueMessageReceiver;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;

/**
 * Created by dobadoba on 7/8/17.
 */
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    private MessageRepository messageRepository;
    private RabbitTemplate rabbitTemplate;
    private String queueName;

    public MessageServiceImpl(MessageRepository messageRepository, RabbitTemplate rabbitTemplate, String queueName) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    @Override
    public Message processMessage(Msg msg) {
        final Message messageEntity = messageRepository.save(buildMessageEntity(msg));
        rabbitTemplate.convertAndSend(queueName,msg);
        return messageEntity;
    }

    @Override
    public Message updateMessage(Msg message) {
        Message messageEntity = messageRepository.findByRef(message.getRef());
        if(messageEntity!=null){
            messageEntity.setStatus(Status.RECEIVED_AT_SINK);
            messageEntity=messageRepository.save(messageEntity);
            LOGGER.info("Request Received at Sink: {} ",messageEntity);
        }
        return messageEntity;
    }

    private Message buildMessageEntity(Msg messageReceived){
        Message messageEntity = new Message();
        messageEntity.setSource(messageReceived.getSource());
        messageEntity.setPayload(messageReceived.getPayload());
        messageEntity.setRef(messageReceived.getRef());
        return messageEntity;
    }
}
