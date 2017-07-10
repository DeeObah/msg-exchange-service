package zw.co.dobadoba.msgexchange.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import zw.co.dobadoba.msgexchange.service.message.MessageService;
import zw.co.dobadoba.msgexchange.service.rest.data.Msg;
import zw.co.dobadoba.msgexchange.service.rest.receiver.MessageReceiver;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import zw.dobadoba.msgexchange.domain.Message;
import zw.dobadoba.msgexchange.domain.utils.Status;

import static org.mockito.Mockito.when;

/**
 * Created by dobadoba on 7/9/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(MessageReceiver.class)
public class MessageReceiverIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageService messageService;

    @Test
    public void shouldReturnNoContentWhenValidMessageIs() throws Exception {
        Msg msg = TestUtils.getCompleteMessage();
        Message messageEntityAfterSaving = TestUtils.buildMessageEntity(msg);
        messageEntityAfterSaving.setStatus(Status.RECEIVED_AT_EXCHANGE);
        when(messageService.processInboundMessage(msg)).thenReturn(messageEntityAfterSaving);
        this.mvc.perform(post("/rest/msg").content(asJsonString(msg))
                .contentType(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
                andExpect(status().isNoContent());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
