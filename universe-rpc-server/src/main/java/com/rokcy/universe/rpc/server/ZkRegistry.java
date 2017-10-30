package com.rokcy.universe.rpc.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by rocky on 17/10/16.
 */
public class ZkRegistry implements Registry {
    private CuratorFramework curatorFramework;
    private final static int baseSleepTimeMs = 1000; //基础睡眠时间, mills
    private final static int maxRetries = 10; //重试次数
    private String url;
    private String data;
    private PersistentNode persistentNode;

    ZkRegistry(String connectString, String url, String data) {
        this.url = url;
        this.data = data;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        this.curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        byte[] bytesData = data == null ? "".getBytes() : data.getBytes();
        this.persistentNode = new PersistentNode(curatorFramework, CreateMode.EPHEMERAL, false, url, bytesData);
    }

    @Override
    public void register() {
        this.persistentNode.start();
        try {
            boolean created = persistentNode.waitForInitialCreate(3000, TimeUnit.MILLISECONDS);
            if (!created) {
                throw new RuntimeException("zookeeper create node failed, when create the node " + "[" + this.url + "]");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister() {
        try {
            this.persistentNode.close();
        } catch (IOException e) {
            throw new RuntimeException("zookeeper stop node failed, when stop the node " + "[" + this.url + "]");
        }
    }
}
