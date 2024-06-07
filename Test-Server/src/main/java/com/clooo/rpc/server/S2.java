package com.clooo.rpc.server;

import com.clooo.rpc.core.annotation.ServiceScan;
import com.clooo.rpc.core.transport.server.RpcServer;


@ServiceScan
public class S2 {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("192.168.137.1", 8099);
        rpcServer.start();
    }

}
