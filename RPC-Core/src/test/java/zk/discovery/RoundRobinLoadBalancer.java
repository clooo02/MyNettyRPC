package zk.discovery;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer {
    private final AtomicInteger currentIndex;

    public RoundRobinLoadBalancer() {
        this.currentIndex = new AtomicInteger(0);
    }

    public <T> T getNextInstance(List<T> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        int index = Math.abs(currentIndex.getAndIncrement() % instances.size());
        return instances.get(index);
    }
}
