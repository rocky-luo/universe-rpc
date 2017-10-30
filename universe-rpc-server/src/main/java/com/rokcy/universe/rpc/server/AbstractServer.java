package com.rokcy.universe.rpc.server;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by rocky on 17/10/13.
 */
public abstract class AbstractServer implements Server{
    private Map<Class, Object> interfaceHandlerMap;
    private String app;
    private String group;
    private int port;
    private String id;
    private Registry registry;
    private final static String registryUrlPrefix = "/universe/server/";

    AbstractServer(Map<Class, Object> interfaceHandlerMap, String app, String group, int port, String zkConnect) {
        this.interfaceHandlerMap = interfaceHandlerMap;
        this.app = app;
        this.group = group;
        this.port = port;
        this.id = UUID.randomUUID().toString();
        String registerUrl = registerUrl(this.app, this.id);
        ServerInfo serverInfo = new ServerInfo(this.app, ip(), this.port, this.group, this.id,
                Lists.newArrayList(this.interfaceHandlerMap.keySet()));
        String registerData = registerData(serverInfo);
        registry = new ZkRegistry(zkConnect, registerUrl, registerData);
    }

    public String getId() {
        return id;
    }

    private static String ip() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected void register() {
        registry.register();
    }

    protected void unregister(){
        registry.unregister();
    }
    public List<Object> getHandlers() {
        return ImmutableList.copyOf(interfaceHandlerMap.values());
    }

    public String getApp() {
        return app;
    }

    public String getGroup() {
        return group;
    }

    public int getPort() {
        return port;
    }

    public Registry getRegistry() {
        return registry;
    }

    public static String getRegistryUrlPrefix() {
        return registryUrlPrefix;
    }

    private static String registerUrl(String app, String id) {
        return registryUrlPrefix + app + "/" + id;
    }
    private static String registerData(ServerInfo serverInfo) {
        return JSON.toJSONString(serverInfo);
    }
}
