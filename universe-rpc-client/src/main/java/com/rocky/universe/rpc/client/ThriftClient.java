package com.rocky.universe.rpc.client;

import com.rocky.universe.rpc.common.thrift.ThriftHelper;
import com.rocky.universe.rpc.registry.ServerInfo;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rocky on 17/11/16.
 */
public class ThriftClient<T> extends AbstractClient{
    public ThriftClient(String app, String group, Class interfaceClass, String zkConnect) {
        super(app, group, interfaceClass, zkConnect);
    }

    public T getThriftClient() {
        ThriftClientProxy<T> clientProxy = new ThriftClientProxy<T>(this, new RandomSelector<>());
        return clientProxy.proxyClient();
    }
}
