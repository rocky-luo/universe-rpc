package com.rocky.universe.rpc.client;

import com.rocky.universe.rpc.common.utils.CollectionUtils;
import com.rocky.universe.rpc.registry.ServerInfo;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by rocky on 17/12/14.
 */
public abstract class AbstractClientProxy<T> implements ClientProxy<T> {
    private List<ProxyProcessor> proxyProcessors;

    public AbstractClientProxy(List<ProxyProcessor> proxyProcessors) {
        this.proxyProcessors = proxyProcessors;
    }

    protected Object process(InvokeContext invokeContext, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, TTransportException {
        if (CollectionUtils.isEmpty(this.proxyProcessors)) {
            return call(invokeContext.getServerContext().getServerInfo(), method, args);
        } else {
            Object result = null;
            for (ProxyProcessor proxyProcessor : this.proxyProcessors) {
                proxyProcessor.pre(invokeContext, method, args);
            }
            try {
                result = call(invokeContext.getServerContext().getServerInfo(), method, args);
            } catch (Exception e) {
                for (ProxyProcessor proxyProcessor : this.proxyProcessors) {
                    proxyProcessor.fail(invokeContext, method, args, e);
                }
                throw e;
            }
            for (int i = this.proxyProcessors.size() - 1; i >= 0; i--) {
                this.proxyProcessors.get(i).post(invokeContext, method, args, result);
            }
            return result;
        }
    }

    abstract protected Object call(ServerInfo serverInfo, Method method, Object[] args) throws TTransportException, InvocationTargetException, IllegalAccessException;
}
