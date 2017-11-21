package com.rocky.universe.rpc.client;

import com.rocky.universe.rpc.common.thrift.ThriftHelper;
import com.rocky.universe.rpc.demo.api.ISayHello;
import com.rocky.universe.rpc.demo.api.Person;
import com.rocky.universe.rpc.registry.ServerInfo;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by rocky on 17/11/20.
 */
public class ThriftClientProxy<T> implements InvocationHandler {
    private ThriftClient<T> thriftClient;
    private Selector<ServerInfo> serverSelector;

    public ThriftClientProxy(ThriftClient<T> thriftClient, Selector<ServerInfo> serverSelector) {
        this.thriftClient = thriftClient;
        this.serverSelector = serverSelector;
    }

    public T proxyClient() {
        Class[] ifaces = {this.thriftClient.getInterfaceClass()};
        return (T) Proxy.newProxyInstance(this.thriftClient.getClass().getClassLoader(),
                ifaces,
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServerInfo selectedServer = this.serverSelector.select(this.thriftClient.availableServers().toArray(new ServerInfo[0]));
        if (selectedServer == null) {
            throw new RuntimeException("there is no server available!");
        }
        Class interfaceClass = this.thriftClient.getInterfaceClass();
        TTransport transport = new TFramedTransport(new TSocket(selectedServer.getIp(), selectedServer.getPort()));
        TProtocol protocol = new TCompactProtocol(transport);
        if (selectedServer.getInterfaces().size() > 1) {
            protocol = new TMultiplexedProtocol(protocol, interfaceClass.getName());
        }
        Class thriftServiceClass = ThriftHelper.getThriftServiceClassByIfaceClass(interfaceClass);
        Class<T> clientClass = ThriftHelper.getClientClass(thriftServiceClass);
        Constructor<T> clientConstructor = null;
        try {
            clientConstructor = clientClass.getConstructor(TProtocol.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        T client = clientConstructor.newInstance(protocol);
        try {
            transport.open();
            return method.invoke(client, args);
        }finally {
            transport.close();
        }
    }
}
