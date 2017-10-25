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
        registry = new ZkRegistry(zkConnect);
    }

    public String getId() {
        return id;
    }

    protected String ip() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected void register() {
        String registryUrl = registryUrlPrefix + app + "/" + getId();
        List<Class> interfaces = Lists.newArrayList(interfaceHandlerMap.keySet());
        ServerInfo serverInfo = new ServerInfo(app, ip(), port, group, getId(), interfaces);
        registry.register(registryUrl, JSON.toJSONString(serverInfo));
    }

    protected void unregister(){
        String registryUrl = registryUrlPrefix + app + "/" + getId();
        registry.unregister(registryUrl);
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
}
