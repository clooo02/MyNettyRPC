package zk.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceInstance;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        try {
            // 创建Curator客户端
            CuratorFramework client = CuratorClient.createCuratorClient();

            // 创建服务注册器
            ServiceRegistrar registrar = new ServiceRegistrar(client);

            // 注册服务
            registrar.registerService("rpc-service", "127.0.0.1", 8080);
            registrar.registerService("rpc-service", "192.168.1.1", 8081);
            registrar.registerService("rpc-service", "192.168.15.1", 8082);

            // 创建服务发现器
            ServiceDiscoverer discoverer = new ServiceDiscoverer(client);

            // 发现服务
//            Collection<ServiceInstance<Object>> instances = discoverer.discoverServices("rpc-service");
//            for (ServiceInstance<Object> instance : instances) {
//                System.out.println("Service found: " + instance.getAddress() + ":" + instance.getPort());
//            }

            for (int i = 0; i < 10; i++) {
                InetSocketAddress instance = discoverer.getNextInstance("rpc-service");
                System.out.println("Service found: " + instance.getAddress() + ":" + instance.getPort());

            }

            // 关闭注册器和发现器
            registrar.close();
            discoverer.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
