package com.clooo.rpc.server.service;


import com.clooo.rpc.api.HelloService;
import com.clooo.rpc.core.annotation.Service;

@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {
//        int i = 1 / 0;
        return "Hello, " + msg;
    }
}