package com.clooo.rpc.core.provider;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManagement  {

    private  final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    public  void addService(String interfaceName, Object service)   {
        if (serviceMap.containsKey(interfaceName)) return;
        serviceMap.put(interfaceName, service);
    }

    public  Object getService(String name) {
        return serviceMap.get(name);
    }

    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }
}
