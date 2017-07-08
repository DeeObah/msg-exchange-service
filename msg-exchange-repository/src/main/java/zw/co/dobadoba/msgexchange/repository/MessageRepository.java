package zw.co.dobadoba.msgexchange.repository;

import org.springframework.data.repository.CrudRepository;
import zw.dobadoba.msgexchange.domain.Message;

/**
 * Created by dobadoba on 7/8/17.
 */
public interface MessageRepository extends CrudRepository<Message,Long>{

    Message findByRef(String ref);
}
