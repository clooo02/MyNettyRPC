package com.clooo.rpc.core.loadbalancer;

public class LoadBalancerFactory {
    private static final RandomLoadBalancer random = new RandomLoadBalancer();
    private static final RoundRobinLoadBalancer roundRobin = new RoundRobinLoadBalancer();

    public static LoadBalancer getRandomLoadBalancer() {
        return random;
    }

    public static LoadBalancer getRoundRobinLoadBalancer() {
        return roundRobin;
    }
}
