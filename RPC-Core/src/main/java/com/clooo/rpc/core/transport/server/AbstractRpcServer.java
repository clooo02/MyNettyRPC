package com.clooo.rpc.core.transport.server;

import com.clooo.rpc.core.annotation.Service;
import com.clooo.rpc.core.annotation.ServiceScan;
import com.clooo.rpc.core.provider.ServiceManagement;
import com.clooo.rpc.common.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Slf4j
public abstract class AbstractRpcServer {

    protected ServiceManagement serviceManagement = new ServiceManagement();

    abstract void start();

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                log.error("启动类缺少 @ServiceScan 注解");
                throw new ClassNotFoundException("启动类缺少 @ServiceScan 注解");
            }
        } catch (Exception e) {
            log.error("出现未知错误");
            throw new RuntimeException("出现未知错误");
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object service;
                try {
                    service = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        publishService(oneInterface.getCanonicalName(), service);
                    }
                } else {
                    publishService(serviceName, service);
                }
            }
        }
    }

    public <T> void publishService(String interfaceName, T service) {
        serviceManagement.addService(interfaceName, service);
    }

}
