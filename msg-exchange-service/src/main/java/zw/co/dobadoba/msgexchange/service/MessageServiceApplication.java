package zw.co.dobadoba.msgexchange.service;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import zw.co.dobadoba.msgexchange.service.message.MessageService;
import zw.co.dobadoba.msgexchange.service.message.MessageServiceImpl;
import zw.co.dobadoba.msgexchange.service.ratelimit.ConfigurationHolder;
import zw.co.dobadoba.msgexchange.service.ratelimit.SourceRateLimitBuilder;
import zw.co.dobadoba.msgexchange.service.rest.configinfo.ConfigurationProvider;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

/**
 * Created by dobadoba on 7/8/17.
 */
@SpringBootApplication
public class MessageServiceApplication {

     public static void main(String[] args) {
            System.setProperty("spring.profiles.active","development");
            ApplicationContext context = SpringApplication.run(MessageServiceApplication.class, args);
            final Config config = context.getBean(ConfigurationProvider.class).getConfig();
            ConfigurationHolder.setConfig(config);
            ConfigurationHolder.setSinkRateLimit(RateLimiter.create(config.getSinkRate()));
            ConfigurationHolder.setSourceRateLimits(SourceRateLimitBuilder.populateSourceRateLimits(config));
            SourceRateLimitBuilder.populateInitialSourceCounts();
        }
}
