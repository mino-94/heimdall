package com.heimdall.gateway.svc;

import com.heimdall.gateway.values.Values;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class SupportService {


    public HttpMethod gubnMethod(HttpServletRequest request) {
        if (isGetPost(request.getMethod())) {
            return HttpMethod.GET;
        }
        return HttpMethod.POST;
    }

    public String makeUrl(HttpServletRequest request, HttpMethod gubn) {
        String instance = validInstance(request.getServerName());

        if (!StringUtils.isEmpty(instance)) {
            StringBuilder url = new StringBuilder(getSecure(request.isSecure()) + instance + request.getRequestURI());
            String qryStr = request.getQueryString();

            if (isGetPost(gubn.name()) && !StringUtils.isEmpty(qryStr)) {
                url.append("?" + qryStr);
            }
            return url.toString();
        }
        return instance;
    }

    public HttpEntity getEntity(HttpServletRequest request, HttpMethod gubn) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (isGetPost(gubn.name())) {
            return new HttpEntity<>(httpHeaders);
        }
        return new HttpEntity<>(setMultiParam(request), httpHeaders);
    }

    public String validInstance(String host) {
        if (!CollectionUtils.isEmpty(Values.INSTANCE_INFO)) {
            if (!StringUtils.isEmpty(host)) {
                String instance = Values.INSTANCE_INFO.get(host);

                if (!StringUtils.isEmpty(instance)) {
                    return instance;
                }
            } else {
                return String.valueOf(true);
            }
        }
        return null;
    }

    public Map resRecover(Exception e) {
        Map result = new HashMap<>();

        if (e instanceof HttpClientErrorException) {
            result.put("resBody", ((HttpClientErrorException) e).getResponseBodyAsString());
            result.put("statusCode", ((HttpClientErrorException) e).getStatusCode());
        } else if (e instanceof HttpServerErrorException) {
            result.put("resBody", ((HttpServerErrorException) e).getResponseBodyAsString());
            result.put("statusCode", ((HttpServerErrorException) e).getStatusCode());
        } else {
            result = null;
        }
        return result;
    }

    public Map parseMap(HttpServletRequest request) {
        Map param = request.getParameterMap();
        Iterator<String> iterator = param.keySet().iterator();
        String key = "";

        while (iterator.hasNext()) {
            key = iterator.next();
            param.put(key, ((String[])param.get(key))[0]);
        }
        return param;
    }

    private boolean isGetPost(String gubn) {
        if (HttpMethod.GET.matches(gubn)) {
            return true;
        }
        return false;
    }

    private String getSecure(boolean secure) {
        if (secure) {
            return "https://";
        }
        return "http://";
    }

    private MultiValueMap setMultiParam(HttpServletRequest request) {
        MultiValueMap multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.setAll(request.getParameterMap());
        return multiValueMap;
    }

}
