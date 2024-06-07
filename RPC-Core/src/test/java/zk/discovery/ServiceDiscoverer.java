package zk.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

public class ServiceDiscoverer {
    private final ServiceDiscovery<Object> serviceDiscovery;

    private final RoundRobinLoadBalancer loadBalancer;


    public ServiceDiscoverer(CuratorFramework client) throws Exception {
        serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class)
                .client(client)
                .basePath("/services")
                .build();
        serviceDiscovery.start();

        loadBalancer = new RoundRobinLoadBalancer();
    }

    public Collection<ServiceInstance<Object>> discoverServices(String serviceName) throws Exception {
        return serviceDiscovery.queryForInstances(serviceName);
    }

    public InetSocketAddress getNextInstance(String serviceName) throws Exception {

        List<InetSocketAddress> instances = serviceDiscovery.queryForInstances(serviceName).stream()
                .map(instance ->
                        new InetSocketAddress(instance.getAddress(), instance.getPort())).toList();

        return loadBalancer.getNextInstance(instances);
    }

    public void close() throws IOException {
        serviceDiscovery.close();
    }
}
