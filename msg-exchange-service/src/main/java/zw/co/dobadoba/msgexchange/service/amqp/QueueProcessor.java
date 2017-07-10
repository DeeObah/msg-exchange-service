package zw.co.dobadoba.msgexchange.service.amqp;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.dobadoba.msgexchange.service.message.MessageService;
import zw.co.dobadoba.msgexchange.service.message.MessageSource;
import zw.co.dobadoba.msgexchange.service.ratelimit.ConfigurationHolder;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSender;

/**
 * Created by dobadoba on 7/8/17.
 */
@Service
@Order(1)
public class QueueProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProcessor.class);

    private MessageSender messageSender;
    private MessageService messageService;
    @Value("${rest.destination.success.code}")
    private String successCode;


    public QueueProcessor(MessageSender messageSender, MessageService messageService) {
        this.messageSender = messageSender;
        this.messageService = messageService;
     }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public void receiveMessage(Msg message) {
        LOGGER.info("Processing Message from Queue: {}",message);
        messageService.processOutboundMessage(message, MessageSource.FROM_QUEUE);
    }
}
