package com.clooo.rpc.core.loadbalancer;

import com.clooo.rpc.core.registry.ZkClient;
import com.clooo.rpc.core.utils.RpcConstant;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.VersionedValue;

import java.io.IOException;
import java.util.List;

/**
 * 利用ZooKeeper的共享计数器实现客户端对节点的轮询
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final SharedCount sharedCount;

    private static final int MAX_THRESHOLD = Integer.MAX_VALUE - 1000; // 自定义计数器的阈值

    public RoundRobinLoadBalancer() {
        sharedCount = new SharedCount(ZkClient.createZkClient(), RpcConstant.ZK_BASE_PATH + "/" + RpcConstant.RPC_SERVICE + "_lock", 0);
        try {
            this.sharedCount.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start SharedCount", e);
        }
    }

    @Override
    public <T> T getNextInstance(List<T> instances) {

        if (instances == null || instances.isEmpty()) {
            return null;
        }
        try {
            VersionedValue<Integer> versionedValue = sharedCount.getVersionedValue();
            int currentValue = versionedValue.getValue();

            if (currentValue >= MAX_THRESHOLD) {
                sharedCount.trySetCount(versionedValue, 0);
                currentValue = 0;
            }

            int index = currentValue % instances.size();
            sharedCount.trySetCount(versionedValue, versionedValue.getValue() + 1);
            return instances.get(index);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sharedCount.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
