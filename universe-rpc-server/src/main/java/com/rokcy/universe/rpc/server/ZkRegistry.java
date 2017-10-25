package com.rokcy.universe.rpc.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * Created by rocky on 17/10/16.
 */
public class ZkRegistry implements Registry {
    private CuratorFramework curatorFramework;
    private final static int baseSleepTimeMs = 1000; //基础睡眠时间, mills
    private final static int maxRetries = 3; //重试次数

    ZkRegistry(String connectString) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        this.curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
    }

    public void register(String url, String data) {
        pathCreateRegister(url, data);
    }

    // TODO: 17/10/23 测试两种创建临时节点方式有何不同
    private void nodeRegister(String url, String data) {
        byte[] bytesData = data == null ? null : data.getBytes();
        PersistentNode persistentNode = new PersistentNode(curatorFramework, CreateMode.EPHEMERAL, false, url, bytesData);
        persistentNode.start();
        try {
            boolean created = persistentNode.waitForInitialCreate(3000, TimeUnit.MILLISECONDS);
            if (!created) {
                throw new RuntimeException("zookeeper create node failed, when create the node " + "[" + url + "]");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private void pathCreateRegister(String url, String data) {
        byte[] bytesData = data == null ? null : data.getBytes();
        try {
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(url, bytesData);
        } catch (Exception e) {
            throw new RuntimeException("zookeeper create node failed, when create the node " + "[" + url + "]");
        }

    }

    public void unregister(String url) {
        try {
            curatorFramework.delete().forPath(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
