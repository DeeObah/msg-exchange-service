package zw.co.dobadoba.msgexchange.service.ratelimit;

import com.revinate.guava.util.concurrent.RateLimiter;
import lombok.Data;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

import java.util.Map;

/**
 * Created by dobadoba on 7/8/17.
 */
@Data
public final class ConfigurationHolder {

    private static Config config;
    private static RateLimiter sinkRateLimit;
    private static Map<String,RateLimiter> sourceRateLimits;

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        ConfigurationHolder.config = config;
    }

    public static RateLimiter getSinkRateLimit() {
        return sinkRateLimit;
    }

    public static void setSinkRateLimit(RateLimiter sinkRateLimit) {
        ConfigurationHolder.sinkRateLimit = sinkRateLimit;
    }

    public static Map<String, RateLimiter> getSourceRateLimits() {
        return sourceRateLimits;
    }

    public static void setSourceRateLimits(Map<String, RateLimiter> sourceRateLimits) {
        ConfigurationHolder.sourceRateLimits = sourceRateLimits;
    }
}
