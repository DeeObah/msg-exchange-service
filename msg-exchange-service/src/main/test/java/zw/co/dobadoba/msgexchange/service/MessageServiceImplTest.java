package zw.co.dobadoba.msgexchange.service;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.message.MessageServiceImpl;
import zw.co.dobadoba.msgexchange.service.ratelimit.ConfigurationHolder;
import zw.co.dobadoba.msgexchange.service.ratelimit.SourceRateLimitBuilder;
import zw.co.dobadoba.msgexchange.service.rest.RestUtils;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSender;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by dobadoba on 7/9/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageSender messageSender;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private ConfigurationHolder configurationHolder;
    private final String queueName ="queue";
    private final RateLimiter sinkRateLimiter = RateLimiter.create(1);
    private final Config config = new Config(buildSourceCountsMap("src1",2),12);
    private MessageServiceImpl messageService;

    @Before
    public void setUp(){
        messageService = new MessageServiceImpl(messageRepository,rabbitTemplate,messageSender,queueName);
        messageService.setSinkRateLimiter(sinkRateLimiter);
        messageService.setSuccessCode("200");
        SourceRateLimitBuilder.setSourceMessageTotals(TestUtils.getSourceLimiterMap(buildSourceCountsMap("src1",2)));
        SourceRateLimitBuilder.setSourceRateLimits(TestUtils.getRateLimiterMap(buildMap(2,"src1-ref","src1","src1 payload")));
        ConfigurationHolder.setConfig(new Config(buildSourceCountsMap("src1",1),12));
        ConfigurationHolder.setSinkRateLimit(RateLimiter.create(ConfigurationHolder.getConfig().getSinkRate()));
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfMessageIsNull() {
        messageService.processInboundMessage(null);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfSourceIsMissing() {
        Msg msg = TestUtils.getCompleteMessage();
        msg.setSource(null);
        messageService.processInboundMessage(msg);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfMsgRefIsMissing() {
        Msg msg = TestUtils.getCompleteMessage();
        msg.setRef(null);
        messageService.processInboundMessage(msg);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfMsgPayloadIsMissing() {
        Msg msg = TestUtils.getCompleteMessage();
        msg.setPayload(null);
        messageService.processInboundMessage(msg);
    }

    @Test
    public void shouldProcessAndReturnAMessageEntityIfAllMsgParameterAreValid() {
        final Msg msg = TestUtils.getCompleteMessage();
        final Message messageToSave=TestUtils.buildMessageEntity(msg);
        final Message messageEntityAfterSaving =TestUtils.buildMessageEntity(msg);
        messageEntityAfterSaving.setStatus(Status.RECEIVED_AT_EXCHANGE);
        when(messageRepository.save(TestUtils.buildMessageEntity(msg))).thenReturn(messageEntityAfterSaving);
        when(messageSender.postToDestination(msg)).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(messageRepository.findByRef(msg.getRef())).thenReturn(messageToSave);
        when(messageRepository.save(messageToSave)).thenReturn(messageEntityAfterSaving);
        doNothing().when(rabbitTemplate).convertAndSend(queueName,msg);
        Message messageSaved = messageService.processInboundMessage(msg);
        Assert.assertEquals("Failed to save and return message",messageEntityAfterSaving,messageSaved);
     }

    @Test
    public void shouldNotPostMessageAndReturnNullIfSinkRateIsExceeded() {
        final Msg msg = TestUtils.getCompleteMessage();
        final Message messageToSave=TestUtils.buildMessageEntity(msg);
        final Message messageEntityAfterSaving =TestUtils.buildMessageEntity(msg);
        messageService.setSinkRateLimiter(RateLimiter.create(1));
        Map<String,RateLimiter> rateLimiterMap=SourceRateLimitBuilder.getSourceRateLimits();
        rateLimiterMap.put(msg.getSource(),RateLimiter.create(2));
        SourceRateLimitBuilder.setSourceRateLimits(rateLimiterMap);
        messageEntityAfterSaving.setStatus(Status.RECEIVED_AT_SINK);
        when(messageRepository.save(TestUtils.buildMessageEntity(msg))).thenReturn(messageEntityAfterSaving);
        when(messageSender.postToDestination(msg)).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(messageRepository.findByRef(msg.getRef())).thenReturn(messageToSave);
        when(messageRepository.save(messageToSave)).thenReturn(messageEntityAfterSaving);
        doNothing().when(rabbitTemplate).convertAndSend(queueName,msg);
        Message firstMessage= messageService.processInboundMessage(msg);
        Message messageSaved = messageService.processInboundMessage(msg);
        Assert.assertEquals(messageEntityAfterSaving,firstMessage);
        Assert.assertNull("Faled to cater for excedded sink rate",messageSaved);
    }

    @Test
    public void shouldIgnoreOtherMessagesAndPostOthersIfSourceIsExceeded() {
        final Msg msg = TestUtils.getCompleteMessage();
        final Message messageToSave=TestUtils.buildMessageEntity(msg);
        final Message messageEntityAfterSaving =TestUtils.buildMessageEntity(msg);
        messageService.setSinkRateLimiter(RateLimiter.create(4));
        Map<String,RateLimiter> rateLimiterMap=SourceRateLimitBuilder.getSourceRateLimits();
        SourceRateLimitBuilder.setSourceRateLimits(rateLimiterMap);
        messageEntityAfterSaving.setStatus(Status.RECEIVED_AT_EXCHANGE);
        when(messageRepository.save(TestUtils.buildMessageEntity(msg))).thenReturn(messageEntityAfterSaving);
        when(messageSender.postToDestination(msg)).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(messageRepository.findByRef(msg.getRef())).thenReturn(messageToSave);
        when(messageRepository.save(messageToSave)).thenReturn(messageEntityAfterSaving);
        doNothing().when(rabbitTemplate).convertAndSend(queueName,msg);
        Message firstMessage = messageService.processInboundMessage(msg);
        Message secondMessage = messageService.processInboundMessage(msg);
        Assert.assertEquals(messageEntityAfterSaving,firstMessage);
        Assert.assertNull("Failed to ignore excess messages ",secondMessage);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionOnMessageUpdateIfMsgIsNotFound() {
        final Msg msg = TestUtils.getCompleteMessage();
        when(messageRepository.findByRef(msg.getRef())).thenReturn(null);
        messageService.updateMessage(msg);
    }

    public void shouldReturnUpdatedMessageUpdateIfMsgIsNotFound() {
        final Msg msg = TestUtils.getCompleteMessage();
        final Message expectedReturnMessage = TestUtils.buildMessageEntity(msg);
        expectedReturnMessage.setStatus(Status.RECEIVED_AT_SINK);
        when(messageRepository.findByRef(msg.getRef())).thenReturn(expectedReturnMessage);
        messageService.updateMessage(msg);
        Assert.assertEquals("Updating message failed",expectedReturnMessage,TestUtils.buildMessageEntity(msg));
    }

    private Map<Integer,Msg> buildMap(Integer rate, String ref,String source,String payload){
        Msg msg= new Msg();
        msg.setPayload(payload);
        msg.setSource(source);
        msg.setRef(ref);
        final HashMap<Integer,Msg> map = new HashMap<>();
        map.put(rate,msg);
        return map;
    }

    private Map<String,Integer> buildSourceCountsMap(String source,Integer rate){
        Map<String,Integer> map = new HashMap<>();
        map.put(source,rate);
        return map;
    }

}
