package com.clooo.rpc.core.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public <T> T getNextInstance(List<T> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        return instances.get(new Random().nextInt(instances.size()));
    }
}
