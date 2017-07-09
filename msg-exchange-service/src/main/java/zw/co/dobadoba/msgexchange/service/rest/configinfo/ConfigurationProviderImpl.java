package zw.co.dobadoba.msgexchange.service.rest.configinfo;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import zw.co.dobadoba.msgexchange.service.rest.RestUtils;
import zw.co.dobadoba.msgexchange.service.rest.data.Config;

/**
 * Created by dobadoba on 7/8/17.
 */
public class ConfigurationProviderImpl implements ConfigurationProvider {


    private RestTemplate restTemplate;
    private String getConfigUrl;

    public ConfigurationProviderImpl(RestTemplate restTemplate, String getConfigUrl) {
        this.restTemplate = restTemplate;
        this.getConfigUrl = getConfigUrl;
    }

    @Override
    public Config getConfig() {

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+getConfigUrl+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        final HttpEntity<?> requestEntity = new HttpEntity<>(RestUtils.getRequestHeaders());
        final HttpEntity<Config> response = restTemplate.exchange(
                getConfigUrl, HttpMethod.GET, requestEntity,Config.class);
        return  response.getBody();
    }
}
