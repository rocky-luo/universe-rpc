package com.rocky.universe.rpc.common.thrift;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TServiceClient;

/**
 * Created by rocky on 17/10/20.
 */
public class ThriftHelper {
    private static boolean isIfaceClass(Class targetClass) {
        return targetClass.getName().endsWith("$Iface");
    }
    public static Class getThriftServiceIface(Class ifaceImplClass) {
        Class iface = null;
        Class clazzIdx = ifaceImplClass;
        while (clazzIdx != null && !clazzIdx.isInterface()) {
            Class[] ifcs = clazzIdx.getInterfaces();
            for (Class ifcIdx : ifcs) {
                if (isIfaceClass(ifcIdx)) {
                    if (iface == null) {
                        iface = ifcIdx;
                    } else {
                        throw new IllegalArgumentException("ifaceImplClass [" + ifaceImplClass.getName() +
                                "] can not implements more than" +
                                " one Iface interface, there is [" + iface.getName() +
                                "] and [" + ifcIdx.getName() + "] found!");
                    }
                }
            }
            clazzIdx = clazzIdx.getSuperclass();
        }
        if (iface == null) {
            throw new IllegalArgumentException("ifaceImplClass [" + ifaceImplClass.getName() + "] must implement one Iface interface!");
        }
        return iface;
    }

    public static Class getThriftServiceClassByIfaceClass(Class iface) {
        return iface.getDeclaringClass();
    }

    public static Class getThriftServiceClassByProcessClass(Class process) {
        return process.getDeclaringClass();
    }

    public static Class getIfaceClassByThriftServiceClass(Class thriftService) {
        Class[] declaredClasses = thriftService.getDeclaredClasses();
        for (Class d : declaredClasses) {
            if (isIfaceClass(d)) {
                return d;
            }
        }
        return null;
    }

    public static Class<TProcessor> getTProcessorClass(Class thriftService) {
        Class<TProcessor> processorClass = null;
        Class[] declaredClasses = thriftService.getDeclaredClasses();
        for (Class c : declaredClasses) {
            if (TBaseProcessor.class.isAssignableFrom(c)) {
                processorClass = (Class<TProcessor>) c;
                break;
            }
        }
        if (processorClass == null) {
            throw new IllegalArgumentException("no TProcessor found in class [" + thriftService.getName() + "]");
        }
        return processorClass;
    }

    public static Class getIfaceClassByImpl(Object impl) {
        Class[] interfaces = impl.getClass().getInterfaces();
        for (Class it : interfaces) {
            if (isIfaceClass(it)) {
                return it;
            }
        }
        return null;
    }

    public static Class getClientClass(Class thriftService) {
        Class clientClass = null;
        Class[] declaredClassed = thriftService.getDeclaredClasses();
        for (Class c :declaredClassed) {
            if (TServiceClient.class.isAssignableFrom(c)) {
                clientClass = c;
                break;
            }
        }
        if (clientClass == null) {
            throw new IllegalArgumentException("no TServiceClient found in class [" + thriftService.getName() + "]");
        }
        return clientClass;
    }
}
