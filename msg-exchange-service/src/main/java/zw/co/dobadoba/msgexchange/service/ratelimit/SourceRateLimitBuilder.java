package zw.co.dobadoba.msgexchange.service.ratelimit;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dobadoba on 7/9/17.
 */
public final class SourceRateLimitBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceRateLimitBuilder.class);

    public static final Map<String,RateLimiter> populateSourceRateLimits(final Config config){
        final Map<String,Integer> sourceRates = config!=null ? config.getConfig() :null;
        if(sourceRates==null || sourceRates.size()==0){
            throw new MessageExchangeException("Error while getting application configurations: Empty Config information");
        }
        final Map<String,RateLimiter> rateLimits= sourceRates.entrySet().stream()
                .collect(Collectors.toMap(p -> p.getKey(),p -> RateLimiter.create(p.getValue())));
        LOGGER.info("Successfully loaded source rate limits for the application as follow: {}",rateLimits);
        return rateLimits;
    }

}
