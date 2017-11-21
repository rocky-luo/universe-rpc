package com.rocky.universe.rpc.demo.server;

import com.google.common.collect.Lists;
import com.rokcy.universe.rpc.server.Server;
import com.rokcy.universe.rpc.server.ThriftThreadedSelectorServer;

import java.util.List;

/**
 * Created by rocky on 17/11/21.
 */
public class ServerMain {
    public static void main(String[] args) {
        List<Object> handlers = Lists.newArrayList(new SayHelloHandler(), new SayGoodByeHandler());
        Server server = new ThriftThreadedSelectorServer(handlers, "test", "default", 8421,"192.168.60.40:2181", 2);
        server.start();
        while (true);
    }
}
