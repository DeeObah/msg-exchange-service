package zw.co.dobadoba.msgexchange.service;

import com.revinate.guava.util.concurrent.RateLimiter;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;

import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.transform.Source;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dobadoba on 7/9/17.
 */
public final class TestUtils {
    private TestUtils(){}

    public static final Msg getCompleteMessage(){
        Msg msg = new Msg();
        msg.setSource("src1");
        msg.setPayload("Msg Payload");
        msg.setRef("scr1_ref");
        return msg;
    }

    public static final  Message buildMessageEntity(Msg msg){
        final Message messageEntity= new Message();
        messageEntity.setPayload(msg.getPayload());
        messageEntity.setRef(msg.getRef());
        messageEntity.setSource(msg.getSource());
        return messageEntity;
    }

    public static final Map<String,RateLimiter> getRateLimiterMap(Map<Integer,Msg> msgMap){
        final Map<String,RateLimiter> rateLimiterMap = new HashMap<>();
        msgMap.entrySet().stream().forEach(msg -> rateLimiterMap.put(msg.getValue().getSource(),RateLimiter.create(msg.getKey())));
        System.out.println(rateLimiterMap);
        return rateLimiterMap;
    }

    public static final Map<String,Integer> getSourceLimiterMap(Map<String,Integer> sourceMap){
        final Map<String,Integer> map = new HashMap<>();
        sourceMap.entrySet().stream().forEach(source -> map.put(source.getKey(),source.getValue()));
        return map;
    }

    private Msg buildMessage(String source,String ref, String payload){
        final Msg msg= new Msg();
        msg.setPayload(source);
        msg.setRef(ref);
        msg.setSource(payload);
        return msg;
    }


}
