package com.rocky.universe.rpc.client;

import com.rocky.universe.rpc.registry.ServerInfo;

import java.util.Set;

/**
 * Created by rocky on 17/12/13.
 */
public class FailedIgnoreClientProxy<T> extends RetryThriftClientProxy<T> {
    public FailedIgnoreClientProxy(ThriftClient<T> thriftClient, Selector<ServerInfo> serverSelector) {
        super(thriftClient, serverSelector);
    }

    @Override
    protected ServerInfo selectServer(Set<ServerInfo> failedServers) {
        return this.serverSelector.select(this.thriftClient.availableServers().toArray(new ServerInfo[0]),
                failedServers.toArray(new ServerInfo[0]));
    }
}
