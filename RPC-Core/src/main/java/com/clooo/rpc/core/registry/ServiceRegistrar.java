package com.clooo.rpc.core.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import static com.clooo.rpc.core.utils.RpcConstant.ZK_BASE_PATH;

public class ServiceRegistrar {
    private final CuratorFramework client;

    public ServiceRegistrar(CuratorFramework client) {
        this.client = client;
    }

    public void registerService(String serviceName, String address, int port) throws Exception {
        String servicePath = ZK_BASE_PATH + "/" + serviceName;
        String instancePath = servicePath + "/" + address + ":" + port;
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(instancePath, (address + ":" + port).getBytes());
    }
}