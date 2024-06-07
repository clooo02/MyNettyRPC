import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class TestZookeeper {

    public static void main(String[] args) {
        try (CuratorFramework curatorFramework = new TestZookeeper().startZookeeper()) {
            curatorFramework.delete().forPath("/myData/aaa");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public CuratorFramework startZookeeper() {
        String zookeeperConnectionString = "192.168.11.128:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        return client;


    }

}
