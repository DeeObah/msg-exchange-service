package zw.co.dobadoba.msgexchange.repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Created by dobadoba on 7/8/17.
 */

@Configuration
@PropertySource("classpath:jpa.properties")
public class JpaVendorConfig {

    @Bean
    public JpaVendorAdapter jpaVendorAdapter(Environment environment) {
        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(environment.getProperty("jpa.show-sql", Boolean.class, false));
        adapter.setGenerateDdl(environment.getProperty("jpa.ddl.generate", Boolean.class, false));
        return adapter;
    }
}
