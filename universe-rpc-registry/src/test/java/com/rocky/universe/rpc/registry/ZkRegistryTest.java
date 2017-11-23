package com.rocky.universe.rpc.registry;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void subscribe() {
        Registry registry = new ZkRegistry(zkConnect, "/hello/register", "nothing");
        registry.subscribe("/rocky", new NotifyListener() {
            @Override
            public void onChanged(NotifyEvent notifyEvent) {
                System.out.println("notify heard, is " + notifyEvent);
            }
        });
        while (true);
    }

    @Test
    public void unsubscribe(){
        Registry registry = new ZkRegistry(zkConnect, "/hello/register", "nothing");
        NotifyListener notifyListener = new NotifyListener() {
            @Override
            public void onChanged(NotifyEvent notifyEvent) {
                System.out.println("notify heard, is " + notifyEvent);
            }
        };
        registry.subscribe("/rocky", notifyListener);
        registry.subscribe("/rocky", notifyListener);
        registry.unSubscribe("/rocky", notifyListener);
        while (true);

    }


    @Test
    public void listenerTest() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 100);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(this.zkConnect, retryPolicy);
        curatorFramework.start();
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, "/rocky", true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("event heard ===> " + event + " " + new String(event.getData().getData()));
            }
        });
        pathChildrenCache.start();
        while (true);

    }

    @Test
    public void listenerTest2() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 100);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(this.zkConnect, retryPolicy);
        curatorFramework.start();
        curatorFramework.getData().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println("=========>" + event);
            }
        }).forPath("/rocky");
        while (true);
    }

}