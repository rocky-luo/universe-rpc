package com.rocky.universe.rpc.client;

import com.rocky.universe.rpc.registry.ServerInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by rocky on 17/12/13.
 */
public class FailedIgnoreClientProxy<T> extends RetryThriftClientProxy<T> {
    public FailedIgnoreClientProxy(List<ProxyProcessor> proxyProcessors, ThriftClient<T> thriftClient, Selector<ServerInfo> serverSelector) {
        super(proxyProcessors, thriftClient, serverSelector);
    }

    public FailedIgnoreClientProxy(ThriftClient<T> thriftClient, Selector<ServerInfo> serverSelector) {
        super(Collections.emptyList(), thriftClient, serverSelector);
    }

    @Override
    protected ServerInfo selectServer(Set<ServerInfo> failedServers) {
        return this.serverSelector.select(this.thriftClient.availableServers().toArray(new ServerInfo[0]),
                failedServers.toArray(new ServerInfo[0]));
    }
}
