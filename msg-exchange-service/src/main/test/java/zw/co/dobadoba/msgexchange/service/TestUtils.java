package zw.co.dobadoba.msgexchange.service;

import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.dobadoba.msgexchange.domain.Message;

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
}
