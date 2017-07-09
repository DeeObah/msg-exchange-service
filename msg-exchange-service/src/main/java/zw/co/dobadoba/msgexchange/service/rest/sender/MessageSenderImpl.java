package zw.co.dobadoba.msgexchange.service.rest.sender;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.rest.RestUtils;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by dobadoba on 7/8/17.
 */
public class MessageSenderImpl implements MessageSender {


    private RestTemplate restTemplate;
    private String destinationUrl;

    public MessageSenderImpl(RestTemplate restTemplate, String destinationUrl) {
        this.restTemplate = restTemplate;
        this.destinationUrl = destinationUrl;
    }

    @Override
    public ResponseEntity<Void> postToDestination(Msg msg) {
        try{
            final RequestEntity<Msg> request = new RequestEntity<>(msg,
                    RestUtils.getRequestHeaders(), HttpMethod.POST, new URI(destinationUrl));
            return restTemplate.exchange(request, Void.class);
        }catch (URISyntaxException uri){
            throw new MessageExchangeException("Error while posting to message to sink",uri.getCause());
        }

    }
}
