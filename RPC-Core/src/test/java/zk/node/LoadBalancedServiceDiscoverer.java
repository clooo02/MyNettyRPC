package zk.node;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancedServiceDiscoverer {
    private final CuratorFramework client;
    private final String serviceName;
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private static final String BASE_PATH = "/services";

    public LoadBalancedServiceDiscoverer(CuratorFramework client, String serviceName) {
        this.client = client;
        this.serviceName = serviceName;
    }

    public String getNextInstance() throws Exception {
        String servicePath = BASE_PATH + "/" + serviceName;
        List<String> instances = client.getChildren().forPath(servicePath);
        if (instances.isEmpty()) {
            return null;
        }
        int index = Math.abs(currentIndex.getAndIncrement() % instances.size());
        return new String(client.getData().forPath(servicePath + "/" + instances.get(index)));
    }
}
