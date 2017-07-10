package zw.co.dobadoba.msgexchange.service.rest.configinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import zw.co.dobadoba.msgexchange.service.rest.RestUtils;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

/**
 * Created by dobadoba on 7/8/17.
 */
public class ConfigurationProviderImpl implements ConfigurationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProviderImpl.class);

    private RestTemplate restTemplate;
    private String getConfigUrl;

    public ConfigurationProviderImpl(RestTemplate restTemplate, String getConfigUrl) {
        this.restTemplate = restTemplate;
        this.getConfigUrl = getConfigUrl;
    }

    @Override
    public Config getConfig() {
        LOGGER.info("Obtaining configuration information from "+getConfigUrl);
        final HttpEntity<?> requestEntity = new HttpEntity<>(RestUtils.getRequestHeaders());
        final ResponseEntity<Config> response = restTemplate.exchange(
                getConfigUrl, HttpMethod.GET, requestEntity,Config.class);
        LOGGER.info("Application initializes with Config information: {}",response.getBody());
        return  response.getBody();
    }
}
