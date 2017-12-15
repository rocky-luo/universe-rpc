package com.rocky.universe.rpc.client;

import com.rocky.universe.rpc.registry.ServerInfo;

/**
 * Created by rocky on 17/12/14.
 */
public class ServerContext {
    private String id;
    private int port;
    private String ip;

    private ServerInfo serverInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
