package com.gateway.heimdall.svc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class RetryService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SupportService supportService;


    @Retryable(value = Exception.class, maxAttempts = 2, backoff = @Backoff(delay = 3000))
    public ResponseEntity retry(HttpServletRequest request) {
        HttpMethod gubn = supportService.gubnMethod(request);

        try {
            return restTemplate.exchange(supportService.makeUrl(request, gubn), gubn, supportService.getEntity(request, gubn), String.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Recover
    private ResponseEntity recover(Exception e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        Map result = supportService.resRecover(e);
        String res = "";

        if (!CollectionUtils.isEmpty(result)) {
            res = result.get("resBody").toString();
            httpStatus = (HttpStatus) result.get("statusCode");
        }
        log.error(printStackTrace(e));

        return new ResponseEntity(res, httpStatus);
    }

    private String printStackTrace(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

}
