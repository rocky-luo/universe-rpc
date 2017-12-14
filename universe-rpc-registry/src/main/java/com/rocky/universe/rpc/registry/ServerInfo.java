package com.rocky.universe.rpc.registry;

import java.util.List;

/**
 * Created by rocky on 17/10/16.
 */
public class ServerInfo {
    private String app;
    private String ip;
    private int port;
    private String group;
    private String id;
    private List<Class> interfaces;

    public ServerInfo(String app, String ip, int port, String group, String id, List<Class> interfaces) {
        this.app = app;
        this.ip = ip;
        this.port = port;
        this.group = group;
        this.id = id;
        this.interfaces = interfaces;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Class> getInterfaces() {
        return interfaces;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "app='" + app + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", group='" + group + '\'' +
                ", id='" + id + '\'' +
                ", interfaces=" + interfaces +
                '}';
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ServerInfo)) {
            return false;
        }else {
            return this.id.equals(((ServerInfo) obj).getId());
        }
    }
}
