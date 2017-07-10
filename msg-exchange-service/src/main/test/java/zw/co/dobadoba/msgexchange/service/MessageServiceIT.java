package zw.co.dobadoba.msgexchange.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import zw.co.dobadoba.msgexchange.service.config.ServiceConfig;
import zw.co.dobadoba.msgexchange.service.message.MessageServiceImpl;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by dobadoba on 7/9/17.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
public class MessageServiceIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageServiceImpl messageService;


    @Test
    public void updateMessageWhenCalledOnExistingMessageShouldUpdateStatusInDatabaseAndReturnUpdatedMessage() throws Exception {
        Message existingMessage = this.entityManager.persist(buildMessage("source1","refsrc1","Message1"));
        final Status statusBeforeUpdate = existingMessage.getStatus();
        Message messageUpdated= messageService.updateMessage(getMessageFromQue());
        assertEquals(existingMessage.getId(),messageUpdated.getId());
        assertEquals(existingMessage.getSource(),messageUpdated.getSource());
        assertEquals(existingMessage.getPayload(),messageUpdated.getPayload());
        assertEquals(existingMessage.getRef(),messageUpdated.getRef());
        assertNotEquals(statusBeforeUpdate,messageUpdated.getStatus());
    }


    private Message buildMessage(String source,String ref, String payload){
        final Message messageEntity= new Message();
        messageEntity.setPayload(source);
        messageEntity.setRef(ref);
        messageEntity.setSource(payload);
        messageEntity.setStatus(Status.RECEIVED_AT_EXCHANGE);
        return messageEntity;
    }
    private Msg getMessageFromQue(){
        Msg msg = new Msg();
        msg.setSource("source1");
        msg.setPayload("Message1");
        msg.setRef("refsrc1");
        return msg;
    }

    @TestConfiguration
    @Import({ServiceConfig.class})
    public static class GetServiceBeans{

        @Bean
        public ConnectionFactory connectionFactory(){
            return new SimpleRoutingConnectionFactory();
        }
        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            return new RabbitTemplate(connectionFactory);
        }
    }
}
