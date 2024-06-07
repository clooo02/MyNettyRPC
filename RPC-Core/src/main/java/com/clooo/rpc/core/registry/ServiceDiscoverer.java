package com.clooo.rpc.core.registry;

import com.clooo.rpc.core.loadbalancer.LoadBalancer;
import com.clooo.rpc.core.loadbalancer.LoadBalancerFactory;
import com.clooo.rpc.core.loadbalancer.RandomLoadBalancer;
import com.clooo.rpc.core.loadbalancer.RoundRobinLoadBalancer;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.clooo.rpc.core.utils.RpcConstant.ZK_BASE_PATH;

public class ServiceDiscoverer {
    private final CuratorFramework client;
    private final String serviceName;
    private final LoadBalancer loadBalancer;

    public ServiceDiscoverer(CuratorFramework client, String serviceName) {
        this.client = client;
        this.serviceName = serviceName;
        loadBalancer = LoadBalancerFactory.getRoundRobinLoadBalancer();
    }

    public String getNextInstance() throws Exception {
        String servicePath = ZK_BASE_PATH + "/" + serviceName;
        List<String> instances = client.getChildren().forPath(servicePath);
        if (instances.isEmpty()) {
            return null;
        }
        String instance = loadBalancer.getNextInstance(instances);
        if (instance == null) {
            return null;
        }
        return new String(client.getData().forPath(servicePath + "/" + instance));
    }
}
