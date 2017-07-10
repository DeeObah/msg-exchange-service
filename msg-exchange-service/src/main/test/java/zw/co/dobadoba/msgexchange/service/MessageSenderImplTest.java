package zw.co.dobadoba.msgexchange.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSenderImpl;

import static org.mockito.Mockito.when;

/**
 * Created by dobadoba on 7/9/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageSenderImplTest {

    @Mock
    private RestTemplate restTemplate;
    private final String destinationUrl="http://locahost:8080/testdestination";
    private MessageSenderImpl messageSender;

    @Before
    public void setUp(){
        messageSender = new MessageSenderImpl(restTemplate,destinationUrl);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfMessageIsNull() {
        messageSender.postToDestination(null);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfSourceIsMissing() {
        Msg msg = TestUtils.getCompleteMessage();
        msg.setSource(null);
        messageSender.postToDestination(msg);
    }


    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfMsgRefIsMissing() {
        Msg msg = TestUtils.getCompleteMessage();
        msg.setRef(null);
        messageSender.postToDestination(msg);
    }

    @Test(expected = MessageExchangeException.class)
    public void shouldThrowExceptionIfMsgPayloadIsMissing() {
        Msg msg = TestUtils.getCompleteMessage();
        msg.setPayload(null);
        messageSender.postToDestination(msg);
    }

    @Test
    public void shouldPostAndReturnResponseEntityIfAllMsgParametersAreValid() {
        final Msg msg = TestUtils.getCompleteMessage();
        final ResponseEntity<Void> expectedResponse =new ResponseEntity<Void>(HttpStatus.OK);
        when(messageSender.postToDestination(msg)).thenReturn(expectedResponse);
        ResponseEntity<Void>  responseObtained = messageSender.postToDestination(msg);
        Assert.assertEquals("Failed post message to destination",expectedResponse,responseObtained);
    }
}

