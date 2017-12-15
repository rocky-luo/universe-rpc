package com.rocky.universe.rpc.client;

import com.google.common.collect.Sets;
import com.rocky.universe.rpc.common.thrift.ThriftHelper;
import com.rocky.universe.rpc.registry.ServerInfo;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Set;

/**
 * Created by rocky on 17/11/20.
 */
public abstract class RetryThriftClientProxy<T> extends AbstractClientProxy<T> implements InvocationHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(RetryThriftClientProxy.class);
    protected ThriftClient<T> thriftClient;
    protected Selector<ServerInfo> serverSelector;

    public RetryThriftClientProxy(List<ProxyProcessor> proxyProcessors, ThriftClient<T> thriftClient, Selector<ServerInfo> serverSelector) {
        super(proxyProcessors);
        this.thriftClient = thriftClient;
        this.serverSelector = serverSelector;
    }

    @Override
    public T proxyClient() {
        Class[] ifaces = {this.thriftClient.getInterfaceClass()};
        return (T) Proxy.newProxyInstance(this.thriftClient.getClass().getClassLoader(),
                ifaces,
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Set<ServerInfo> failedServers = Sets.newHashSetWithExpectedSize(10);
        Object result = null;
        boolean needRetry = true;
        while (needRetry) {
            ServerInfo selectedServer = selectServer(failedServers);
            try {
                result = process(invokeContext(selectedServer), method, args);
                needRetry = false;
            } catch (TTransportException e) {
                // 加入失效server集合
                failedServers.add(selectedServer);
                LOGGER.debug("add a failed server {}, current failed servers are {}", selectedServer, failedServers);
            }
        }
        return result;
    }

    @Override
    protected Object call(ServerInfo selectedServer, Method method, Object[] args) throws TTransportException, InvocationTargetException, IllegalAccessException {
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
        T client = null;
        try {
            client = clientConstructor.newInstance(protocol);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        try {
            transport.open();
            return method.invoke(client, args);
        }finally {
            transport.close();
        }
    }

    abstract protected ServerInfo selectServer(Set<ServerInfo> failedServers);

    private InvokeContext invokeContext(ServerInfo serverInfo) {
        InvokeContext invokeContext = new InvokeContext();
        invokeContext.setApp(this.thriftClient.getApp());
        ClientContext clientContext = new ClientContext(this.thriftClient.getId());
        ServerContext serverContext = new ServerContext();
        serverContext.setId(serverInfo.getId());
        serverContext.setIp(serverInfo.getIp());
        serverContext.setPort(serverInfo.getPort());
        serverContext.setServerInfo(serverInfo);
        invokeContext.setClientContext(clientContext);
        invokeContext.setServerContext(serverContext);
        return invokeContext;
    }

}
