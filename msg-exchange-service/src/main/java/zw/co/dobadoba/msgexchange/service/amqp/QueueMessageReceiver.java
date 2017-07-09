package zw.co.dobadoba.msgexchange.service.amqp;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import zw.co.dobadoba.msgexchange.service.message.MessageService;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSender;

/**
 * Created by dobadoba on 7/8/17.
 */
@Service
@Order(1)
public class QueueMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMessageReceiver.class);

    private MessageSender messageSender;
    private MessageService messageService;
    @Value("${rest.destination.success.code}")
    private String successCode;

    public QueueMessageReceiver(MessageSender messageSender, MessageService messageService) {
        this.messageSender = messageSender;
        this.messageService = messageService;
     }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public void receiveMessage(Msg message) {
        LOGGER.info("Received Message from Queue: {}",message);
       ResponseEntity<Void> responseEntity = messageSender.postToDestination(message);
        if (successCode.equalsIgnoreCase(responseEntity.getStatusCode().toString())){
            messageService.updateMessage(message);
        }
    }
}
