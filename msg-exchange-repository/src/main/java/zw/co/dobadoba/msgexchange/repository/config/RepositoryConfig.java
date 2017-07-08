package zw.co.dobadoba.msgexchange.repository.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by dobadoba on 7/8/17.
 */
@Configuration
@Import(ConsolidatedResourceConfig.class)
@EnableJpaRepositories(basePackages="zw.co.dobadoba.msgexchange.repository")
public class RepositoryConfig {

}
