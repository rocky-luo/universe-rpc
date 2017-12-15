package com.rocky.universe.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rocky on 17/11/16.
 */
public class ThriftClient<T> extends AbstractClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftClient.class);

    public ThriftClient(String app, String group, Class interfaceClass, String zkConnect) {
        super(app, group, interfaceClass, zkConnect);
    }

    public T getThriftClient() {
        RetryThriftClientProxy<T> clientProxy = new FailedIgnoreClientProxy<T>(this, new RoundRobinSelector<>());
        return clientProxy.proxyClient();
    }
}
