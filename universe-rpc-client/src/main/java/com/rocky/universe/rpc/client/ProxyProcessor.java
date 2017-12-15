package com.rocky.universe.rpc.client;

import java.lang.reflect.Method;

/**
 * Created by rocky on 17/12/14.
 */
public interface ProxyProcessor {
    void pre(InvokeContext invokeContext, Method method, Object[] args);
    void post(InvokeContext invokeContext, Method method, Object[] args, Object result);
    void fail(InvokeContext invokeContext, Method method, Object[] args, Exception e);
}
