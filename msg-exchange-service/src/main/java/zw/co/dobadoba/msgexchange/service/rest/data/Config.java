package zw.co.dobadoba.msgexchange.service.rest.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by dobadoba on 7/8/17.
 */
public class Config implements Serializable {

    private Map<String,Integer> config;
    private int sinkRate;

    public Config() {
    }

    public Config(Map<String, Integer> config, int sinkRate) {
        this.config = config;
        this.sinkRate = sinkRate;
    }

    public Map<String, Integer> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Integer> config) {
        this.config = config;
    }

    public int getSinkRate() {
        return sinkRate;
    }

    public void setSinkRate(int sinkRate) {
        this.sinkRate = sinkRate;
    }

    @Override
    public String toString() {
        return "Config{" +
                "config=" + config +
                ", sinkRate=" + sinkRate +
                '}';
    }
}
