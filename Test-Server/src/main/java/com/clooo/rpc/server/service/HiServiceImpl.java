package com.clooo.rpc.server.service;



import com.clooo.rpc.api.HiService;
import com.clooo.rpc.core.annotation.Service;

@Service
public class HiServiceImpl implements HiService {
    @Override
    public String hi() {
        return "Hi";
    }
}