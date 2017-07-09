package zw.co.dobadoba.msgexchange.service.rest.sender;

import org.springframework.http.ResponseEntity;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;

/**
 * Created by dobadoba on 7/8/17.
 */
public interface MessageSender  {

    ResponseEntity<Void> postToDestination(Msg msg);
}
