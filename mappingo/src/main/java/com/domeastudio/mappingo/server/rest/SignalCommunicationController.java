package com.domeastudio.mappingo.server.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignalCommunicationController {
    @RequestMapping("/test")
    public String test(){
        return "微服务测试通过";
    }
}
