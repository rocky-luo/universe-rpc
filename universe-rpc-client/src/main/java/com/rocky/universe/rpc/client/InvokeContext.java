package com.rocky.universe.rpc.client;

/**
 * Created by rocky on 17/12/14.
 */
public class InvokeContext {
    private ServerContext serverContext;
    private ClientContext clientContext;
    private String app;

    public ServerContext getServerContext() {
        return serverContext;
    }

    public void setServerContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public ClientContext getClientContext() {
        return clientContext;
    }

    public void setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
