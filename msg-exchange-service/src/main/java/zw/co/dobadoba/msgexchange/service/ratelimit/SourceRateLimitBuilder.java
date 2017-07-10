package zw.co.dobadoba.msgexchange.service.ratelimit;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zw.co.dobadoba.msgexchange.service.exception.MessageExchangeException;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dobadoba on 7/9/17.
 */
public final class SourceRateLimitBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceRateLimitBuilder.class);
    private static Map<String,RateLimiter> sourceRateLimits;
    private static Map<String,Integer> sourceMessageTotals;

    public static Map<String,RateLimiter> populateSourceRateLimits(final Config config){
        final Map<String,Integer> sourceRates = config!=null ? config.getConfig() :null;
         validateSourceRates(sourceRates);
        sourceRateLimits= sourceRates.entrySet().stream()
                .collect(Collectors.toMap(p -> p.getKey(),p -> RateLimiter.create(p.getValue())));
        LOGGER.info("Successfully loaded source rate limits for the application as follow: {}",sourceRateLimits);
        return sourceRateLimits;
    }


    public static Map<String, RateLimiter> getSourceRateLimits() {
        return sourceRateLimits;
    }

    public static Map<String, Integer> getSourceMessageTotals() {
        return sourceMessageTotals;
    }


    public static void setSourceMessageTotals(Map<String, Integer> sourceMessageTotals) {
        SourceRateLimitBuilder.sourceMessageTotals = sourceMessageTotals;
    }

    public static void setSourceRateLimits(Map<String, RateLimiter> sourceRateLimits) {
        SourceRateLimitBuilder.sourceRateLimits = sourceRateLimits;
    }

    public static  void populateInitialSourceCounts(){
        final Config config = ConfigurationHolder.getConfig();
        final Map<String,Integer> sourceRates = config!=null ? config.getConfig() :new HashMap<>();
        validateSourceRates(sourceRates);
        final Map<String,Integer> rateCounts= sourceRates.entrySet().stream()
                .collect(Collectors.toMap(p -> p.getKey(),p -> 0));
        LOGGER.info("Successfully loaded initial source counts rate limits for the application as follow: {}",rateCounts);
        sourceMessageTotals=rateCounts;
    }

    private static void validateSourceRates(  final Map<String,Integer> sourceRates){
        if(sourceRates==null || sourceRates.size()==0){
            throw new MessageExchangeException("Error while getting application configurations: Empty Config information");
        }
    }

    public static void updateSourceTotal(Map<String,Integer> counts){
        sourceMessageTotals=counts;
    }


}
