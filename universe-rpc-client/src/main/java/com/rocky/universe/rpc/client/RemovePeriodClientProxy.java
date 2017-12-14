package com.rocky.universe.rpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rocky.universe.rpc.registry.ServerInfo;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by rocky on 17/12/13.
 */
public class RemovePeriodClientProxy<T> extends RetryThriftClientProxy<T> {
    private long removeTimeMills;
    private Set<RemovedServerInfo> removedServerInfos = Sets.newConcurrentHashSet();
    private ReentrantLock removeSetLock = new ReentrantLock();

    public RemovePeriodClientProxy(ThriftClient thriftClient, Selector serverSelector, long removeTimeMills) {
        super(thriftClient, serverSelector);
        this.removeTimeMills = removeTimeMills;
    }

    @Override
    protected ServerInfo selectServer(Set<ServerInfo> failedServers) {
        if ((failedServers == null || failedServers.isEmpty()) && this.removedServerInfos.isEmpty()) {
            return this.serverSelector.select(this.thriftClient.availableServers().toArray(new ServerInfo[0]));
        } else {
            // TODO: 17/12/13 性能应该会有问题
            this.removeSetLock.lock();
            try {
                long now = new Date().getTime();
                // 删除到期的server
                for (RemovedServerInfo rsi : this.removedServerInfos) {
                    if (rsi.getResumeMills() < now) {
                        this.removedServerInfos.remove(rsi);
                    }
                }
                for (ServerInfo serverInfo : failedServers) {
                    this.removedServerInfos.add(new RemovedServerInfo(serverInfo, now + this.removeTimeMills));
                }
            } finally {
                this.removeSetLock.unlock();
            }
            List<ServerInfo> ignores = Lists.newArrayListWithCapacity(this.removedServerInfos.size());
            for (RemovedServerInfo ignore : this.removedServerInfos) {
                ignores.add(ignore.getServerInfo());
            }
            return this.serverSelector.select(this.thriftClient.availableServers().toArray(new ServerInfo[0]),
                    ignores.toArray(new ServerInfo[0]));
        }
    }

    private class RemovedServerInfo {
        private ServerInfo serverInfo;
        private long resumeMills;

        public RemovedServerInfo(ServerInfo serverInfo, long resumeMills) {
            this.serverInfo = serverInfo;
            this.resumeMills = resumeMills;
        }

        @Override
        public int hashCode() {
            return this.serverInfo.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.serverInfo.equals(obj);
        }

        public ServerInfo getServerInfo() {
            return serverInfo;
        }

        public long getResumeMills() {
            return resumeMills;
        }
    }


}
