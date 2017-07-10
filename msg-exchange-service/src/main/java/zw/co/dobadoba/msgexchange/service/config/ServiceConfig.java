package zw.co.dobadoba.msgexchange.service.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import zw.co.dobadoba.msgexchange.repository.MessageRepository;
import zw.co.dobadoba.msgexchange.repository.config.RepositoryConfig;
import zw.co.dobadoba.msgexchange.service.message.MessageService;
import zw.co.dobadoba.msgexchange.service.message.MessageServiceImpl;
import zw.co.dobadoba.msgexchange.service.ratelimit.ConfigurationHolder;
import zw.co.dobadoba.msgexchange.service.rest.configinfo.ConfigurationProvider;
import zw.co.dobadoba.msgexchange.service.rest.configinfo.ConfigurationProviderImpl;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSender;
import zw.co.dobadoba.msgexchange.service.rest.sender.MessageSenderImpl;

/**
 * Created by dobadoba on 7/8/17.
 */
@Configuration
@Import({RepositoryConfig.class,AmqpConfig.class})
public class ServiceConfig {

    RestTemplate restTemplate = new RestTemplate();

    @Bean
    public ConfigurationProvider configurationProvider(@Value("${rest.getconfig.url}") String getConfigUrl){
        return new ConfigurationProviderImpl(restTemplate,getConfigUrl);
    }

    @Bean
    public MessageService messageService(MessageRepository messageRepository,RabbitTemplate rabbitTemplate,
                                         @Value("${amqp.queue}") String queueName,MessageSender messageSender){
        MessageServiceImpl messageService = new MessageServiceImpl(messageRepository,rabbitTemplate,messageSender,queueName);
        return messageService;
    }

    @Bean
    public MessageSender messageSender(@Value("${rest.destination.url}")String destinationUrl){
        return new MessageSenderImpl(restTemplate, destinationUrl);
    }

}
