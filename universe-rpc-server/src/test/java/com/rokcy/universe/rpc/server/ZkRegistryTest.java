package com.rokcy.universe.rpc.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rocky on 17/10/20.
 */
public class ZkRegistryTest {
    String zkConnect = null;
    @Before
    public void init() {
        zkConnect = "192.168.60.12:2181,192.168.60.25:2181,192.168.60.26:2181,192.168.60.37:2181,192.168.60.38:2181,192.168.60.39:2181,192.168.60.40:2181";
    }
    @Test
    public void curatorTest() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnect, retryPolicy);
        client.start();
        client.create()
                .forPath("/my", "hello every one".getBytes());
    }
    @Test
    public void register() throws Exception {
        Registry registry = new ZkRegistry(zkConnect, "/hello/rocky", "test");
        registry.register();
    }

    @Test
    public void unregister() throws Exception {
        Registry registry = new ZkRegistry(zkConnect, "/hello/unregister", "nothing");
        registry.register();
        Thread.sleep(1000L);
        registry.unregister();
    }

}