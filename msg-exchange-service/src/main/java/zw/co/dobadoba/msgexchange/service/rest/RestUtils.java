package zw.co.dobadoba.msgexchange.service.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

/**
 * Created by dobadoba on 7/8/17.
 */
public final class RestUtils {

    private RestUtils(){}


    public static final HttpHeaders getRequestHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

}
