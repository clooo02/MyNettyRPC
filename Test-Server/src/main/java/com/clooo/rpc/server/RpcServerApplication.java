package com.clooo.rpc.server;

import com.clooo.rpc.core.annotation.ServiceScan;
import com.clooo.rpc.core.transport.server.RpcServer;


@ServiceScan
public class RpcServerApplication {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1", 8088);
        rpcServer.start();
    }

}
