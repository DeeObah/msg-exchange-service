package zw.co.dobadoba.msgexchange.service.rest.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import zw.co.dobadoba.msgexchange.service.message.MessageService;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;

/**
 * Created by dobadoba on 7/8/17.
 */
@RestController
@RequestMapping("/rest")
public class MessageReceiver {

    private MessageService messageService;

    public MessageReceiver(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(value = "/msg", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE} )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void receiveMessage(@RequestBody Msg msg){
         messageService.processInboundMessage(msg);
    }
}
