package com.rocky.universe.rpc.client;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.rocky.universe.rpc.registry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Created by rocky on 17/10/31.
 */
public abstract class AbstractClient implements Client{
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);
    private String app;
    private String group;
    private Class interfaceClass;
    private String zkConnect;
    private Registry registry;
    private String id;
    private Map<String, ServerInfo> idServerMap = Maps.newConcurrentMap();
    volatile boolean idServerMapValid = false;
    private Map<String, ServerInfo> backupIdServerMap = Maps.newConcurrentMap();
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
        //listen 生效需要时间,在listen生效前使用备用map
        List<String> serverDatas = this.registry.lookup("/universe/server/" + this.app);
        for (String sd : serverDatas) {
            ServerInfo serverInfo = JSON.parseObject(sd, ServerInfo.class);
            this.backupIdServerMap.put(serverInfo.getId(), serverInfo);
        }
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
                if (!idServerMapValid) {
                    idServerMapValid = true;
                    LOGGER.debug("app:{}, client turn backup server map into official one", app);
                }
            }
        });
    }

    protected Set<ServerInfo> availableServers() {
        if (this.idServerMapValid) {
            return ImmutableSet.copyOf(this.idServerMap.values());
        }else {
            return ImmutableSet.copyOf(this.backupIdServerMap.values());
        }
    }

    protected Class getInterfaceClass() {
        return interfaceClass;
    }

    public String getApp() {
        return app;
    }

    public String getId() {
        return id;
    }
}
