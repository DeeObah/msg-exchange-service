package zw.co.dobadoba.msgexchange.service.message;

import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.dobadoba.msgexchange.domain.Message;

/**
 * Created by dobadoba on 7/8/17.
 */
public interface MessageService  {

     Message processMessage(Msg message);

     Message updateMessage(Msg message);
}
