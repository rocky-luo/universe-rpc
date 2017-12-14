package com.rocky.universe.rpc.client;

/**
 * Created by rocky on 17/11/16.
 */
public class ThriftClient<T> extends AbstractClient{
    public ThriftClient(String app, String group, Class interfaceClass, String zkConnect) {
        super(app, group, interfaceClass, zkConnect);
    }

    public T getThriftClient() {
        RetryThriftClientProxy<T> clientProxy = new RemovePeriodClientProxy<T>(this, new RoundRobinSelector<>(), 1000L * 30);
        return clientProxy.proxyClient();
    }
}
