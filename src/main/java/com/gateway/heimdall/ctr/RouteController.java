package com.gateway.heimdall.ctr;

import com.gateway.heimdall.svc.RetryService;
import com.gateway.heimdall.svc.SupportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class RouteController {

    @Autowired
    private RetryService retryService;

    @Autowired
    private SupportService supportService;


    @RequestMapping("/**")
    public ResponseEntity getRouter(HttpServletRequest request, HttpServletResponse response) {
        log.info("Gateway Request Method : " + request.getMethod());
        log.info("Gateway Request param : " + supportService.parseMap(request));

        return retryService.retry(request);
    }

    @PostMapping("/ping")
    public String ping(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = null;
        String reason = "";
        int val = 0;

        if (!StringUtils.isEmpty(supportService.validInstance(null))) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        reason = status.getReasonPhrase();
        val = status.value();
        response.setStatus(val);

        log.info("Gateway Ping Check Response Status value : " + reason + "[" + val + "]");

        return reason;
    }

}
