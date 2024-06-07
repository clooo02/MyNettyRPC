package zk.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorClient {
    private static final String ZK_ADDRESS = "192.168.11.128:2181";
    
    public static CuratorFramework createCuratorClient() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
            ZK_ADDRESS,
            new ExponentialBackoffRetry(1000, 3)
        );
        client.start();
        return client;
    }
}