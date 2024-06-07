package com.clooo.rpc.server.service;


import com.clooo.rpc.api.LoginService;
import com.clooo.rpc.core.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public Map<String, String> login(String username, String password) {
        if ("chun".equals(username) && "123".equals(password)) {
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            return map;
        } else return null;
    }
}