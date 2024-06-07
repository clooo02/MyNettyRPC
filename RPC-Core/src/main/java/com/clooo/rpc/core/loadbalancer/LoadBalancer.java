package com.clooo.rpc.core.loadbalancer;

import java.util.List;

public interface LoadBalancer {

    <T> T getNextInstance(List<T> instances);

}