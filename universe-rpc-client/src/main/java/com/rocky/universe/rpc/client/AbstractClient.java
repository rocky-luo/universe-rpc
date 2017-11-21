package com.rocky.universe.rpc.client;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.rocky.universe.rpc.registry.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Created by rocky on 17/10/31.
 */
public abstract class AbstractClient implements Client{
    private String app;
    private String group;
    private Class interfaceClass;
    private String zkConnect;
    private Registry registry;
    private String id;
    private Map<String, ServerInfo> idServerMap = Maps.newHashMapWithExpectedSize(10);
    private final static String registryUrlPrefix = "/universe/client/";

    public AbstractClient(String app, String group, Class interfaceClass, String zkConnect) {
        this.app = app;
        this.group = group;
        this.interfaceClass = interfaceClass;
        this.zkConnect = zkConnect;
        this.id = UUID.randomUUID().toString();
        this.registry = new ZkRegistry(zkConnect, registerUrl(this.app, this.id), null);
    }

    @Override
    public void start() {
        listen();
    }

    @Override
    public void stop() {

    }

    private static String registerUrl(String app, String id) {
        return registryUrlPrefix + app + "/" + id;
    }

    protected void listen() {
        registry.subscribe("/universe/server/" + this.app, new NotifyListener() {
            public void onChanged(NotifyEvent notifyEvent) {
                ServerInfo serverInfo = JSON.parseObject(notifyEvent.getData(), ServerInfo.class);
                if (notifyEvent.getEvent() == NotifyEvent.Event.ADD_NODE ||
                        notifyEvent.getEvent() == NotifyEvent.Event.MODIFY_NODE) {
                    idServerMap.put(serverInfo.getId(), serverInfo);

                }else if (notifyEvent.getEvent() == NotifyEvent.Event.DELETE_NODE) {
                    idServerMap.remove(serverInfo.getId());
                }
            }
        });
    }

    protected Set<ServerInfo> availableServers() {
        return ImmutableSet.copyOf(idServerMap.values());
    }

    protected Class getInterfaceClass() {
        return interfaceClass;
    }
}
