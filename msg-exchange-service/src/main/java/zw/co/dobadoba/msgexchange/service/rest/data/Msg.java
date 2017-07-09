package zw.co.dobadoba.msgexchange.service.rest.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by dobadoba on 7/8/17.
 */
public class Msg implements Serializable{

    private static final Long serialVersionUID = 1L;

    private String id;
    private String source;
    private String payload;
    private String ref;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", payload='" + payload + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}
