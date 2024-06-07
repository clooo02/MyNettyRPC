package zk.node;

import org.apache.curator.framework.CuratorFramework;

public class Main {
    public static void main(String[] args) {
        try {
            // 创建Curator客户端
            CuratorFramework client = ZkClient.createZkClient();

            // 创建服务注册器
            ServiceRegistrar registrar = new ServiceRegistrar(client);

            // 注册三个服务实例
//            registrar.registerService("my-service", "127.0.0.1", 8080);
//            registrar.registerService("my-service", "127.0.0.1", 8081);
            registrar.registerService("my-service", "127.0.0.1", 8082);

            // 创建负载均衡服务发现器
            LoadBalancedServiceDiscoverer discoverer = new LoadBalancedServiceDiscoverer(client, "my-service");

            // 模拟服务请求，获取下一个服务实例
            for (int i = 0; i < 10; i++) {
                String instance = discoverer.getNextInstance();
                if (instance != null) {
                    System.out.println("Using service instance: " + instance);
                } else {
                    System.out.println("No service instances available.");
                }
            }

            // 关闭Curator客户端
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
