package zw.co.dobadoba.msgexchange.service.rest.sender;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.ratelimit.ConfigurationHolder;
import zw.co.dobadoba.msgexchange.service.rest.RestUtils;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by dobadoba on 7/8/17.
 */
public class MessageSenderImpl implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderImpl.class);
    private RestTemplate restTemplate;
    private String destinationUrl;
    @Value("${rest.destination.success.code}")
    private String successCode;
    private int queued=0;
    private int posted=0;
    private int messageCount=0;
    private RateLimiter rateLimiter;


    public MessageSenderImpl(RestTemplate restTemplate, String destinationUrl) {
        this.restTemplate = restTemplate;
        this.destinationUrl = destinationUrl;
    }

    @Override
    public synchronized  ResponseEntity<Void> postToDestination(Msg msg) {
        LOGGER.error("Posting message: {} to destination:  {} {}",msg,destinationUrl);
        validateMessage(msg);
        try{
            final RequestEntity<Msg> request = new RequestEntity<>(msg,
                    RestUtils.getRequestHeaders(), HttpMethod.POST, new URI(destinationUrl));
          return restTemplate.exchange(request, Void.class);
        }catch (URISyntaxException uri){
            LOGGER.error("Failed post message to destination: {}",uri.getMessage());
            throw new MessageExchangeException("Error while posting to message to sink",uri.getCause());
        }
    }

    


    private void validateMessage(Msg msg){
        if(msg==null){
            throw new MessageExchangeException("Cannot post an empty message");
        }
        if(StringUtils.isEmpty(msg.getRef())){
            throw new MessageExchangeException("Field ref cannot empty");
        }
        if(StringUtils.isEmpty(msg.getSource())){
            throw new MessageExchangeException("Field source cannot be empty");
        }
        if(StringUtils.isEmpty(msg.getPayload())){
            throw new MessageExchangeException("Field payload cannot be empty");
        }
    }

}
