package com.clooo.rpc.api;

import java.util.Map;

public interface LoginService {

    Map<String, String> login(String username, String password);

}
