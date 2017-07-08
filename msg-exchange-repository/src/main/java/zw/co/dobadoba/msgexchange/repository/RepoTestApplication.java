package zw.co.dobadoba.msgexchange.repository;

import org.springframework.boot.SpringApplication;
import zw.co.dobadoba.msgexchange.repository.config.RepositoryConfig;

/**
 * Created by dobadoba on 7/8/17.
 */
public class RepoTestApplication {

    public static void main(String [] args){
        System.setProperty("spring.profiles.active","development");
        SpringApplication.run(RepositoryConfig.class);

    }
}
