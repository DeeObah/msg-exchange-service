package zw.co.dobadoba.msgexchange.service.message;

import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.dobadoba.msgexchange.domain.Message;

/**
 * Created by dobadoba on 7/8/17.
 */
public interface MessageService  {

     Message processInboundMessage(Msg message);

     Message processOutboundMessage(Msg msg, MessageSource messageSource);

     Message updateMessage(Msg message);
     Message save(Msg msg);
}
