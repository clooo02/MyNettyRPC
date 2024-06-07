package com.clooo.rpc.client;

import com.clooo.rpc.api.HelloService;
import com.clooo.rpc.core.transport.client.RpcClient;
import com.clooo.rpc.core.transport.client.RpcClientManager;

public class Main {
    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        HelloService helloService = rpcClient.getProxyService(HelloService.class);
//        HelloService helloService = RpcClientManager.getProxyService(HelloService.class);
        System.out.println(helloService.sayHello("looo"));

//        RpcClientManager.shutdown();
    }
}