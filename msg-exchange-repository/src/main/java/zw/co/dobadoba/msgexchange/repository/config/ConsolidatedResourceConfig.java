package zw.co.dobadoba.msgexchange.repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by dobadoba on 7/8/17.
 */
@Configuration
@Import({JpaVendorConfig.class,DevelopmentDataSourceConfig.class})
public class ConsolidatedResourceConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,JpaVendorAdapter vendorAdapter,
                                                                       Environment environment, ResourceLoader resourceLoader) throws IOException {
        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("zw.dobadoba.msgexchange.domain");
        emf.setPersistenceUnitName("MessageExchangePu");
        emf.setJpaVendorAdapter(vendorAdapter);
        final String jpaPropertiesConfigPath = environment.getProperty("jpa.vendor.properties.config");
        if(StringUtils.hasText(jpaPropertiesConfigPath)){
            final Resource jpaPropertiesConfig = resourceLoader.getResource(jpaPropertiesConfigPath);
            emf.setJpaProperties(PropertiesLoaderUtils.loadProperties(jpaPropertiesConfig));
        }
        return emf;
    }

}
