package zw.co.dobadoba.msgexchange.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.message.MessageServiceImpl;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSender;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;


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
    private final String queueName ="queue";

    private MessageServiceImpl messageService;

    @Before
    public void setUp(){
        messageService = new MessageServiceImpl(messageRepository,rabbitTemplate,messageSender,queueName);
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
        final Message messageEntityAfterSaving =TestUtils.buildMessageEntity(msg);
        messageEntityAfterSaving.setStatus(Status.RECEIVED_AT_EXCHANGE);
        when(messageRepository.save(TestUtils.buildMessageEntity(msg))).thenReturn(messageEntityAfterSaving);
        doNothing().when(rabbitTemplate).convertAndSend(queueName,msg);
        Message messageSaved = messageService.processInboundMessage(msg);
        System.out.println(messageEntityAfterSaving);
        Assert.assertEquals("Failed to save and return message",messageEntityAfterSaving,messageSaved);
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
        Assert.assertEquals("Upating message failed",expectedReturnMessage,TestUtils.buildMessageEntity(msg));
    }

}
