package zk.discovery;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import javax.annotation.WillClose;
import java.io.IOException;

public class ServiceRegistrar {
    private static final String BASE_PATH = "/services";
    private final ServiceDiscovery<Object> serviceDiscovery;

    public ServiceRegistrar(CuratorFramework client) throws Exception {
        serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class)
            .client(client)
            .basePath(BASE_PATH)
            .build();
        serviceDiscovery.start();
    }

    public void registerService(String serviceName, String address, int port) throws Exception {
        ServiceInstance<Object> serviceInstance = ServiceInstance.builder()
            .name(serviceName)
            .address(address)
            .port(port)
            .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    public void close() throws IOException {
        serviceDiscovery.close();
    }
}
