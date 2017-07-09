package zw.co.dobadoba.msgexchange.service.config;

import lombok.Data;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

/**
 * Created by dobadoba on 7/8/17.
 */
@Data
public class ConfigurationHolder {

    private static Config config;

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        ConfigurationHolder.config = config;
    }
}
