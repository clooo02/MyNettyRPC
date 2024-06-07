package zk.node;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

public class ServiceRegistrar {
    private final CuratorFramework client;
    private static final String BASE_PATH = "/services";

    public ServiceRegistrar(CuratorFramework client) {
        this.client = client;
    }

    public void registerService(String serviceName, String address, int port) throws Exception {
        String servicePath = BASE_PATH + "/" + serviceName;
        String instancePath = servicePath + "/" + address + ":" + port;
        client.create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.EPHEMERAL)
            .forPath(instancePath, (address + ":" + port).getBytes());
    }
}