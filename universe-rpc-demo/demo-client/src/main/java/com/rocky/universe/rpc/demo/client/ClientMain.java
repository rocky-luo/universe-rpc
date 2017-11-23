package com.rocky.universe.rpc.demo.client;

import com.rocky.universe.rpc.client.ThriftClient;
import com.rocky.universe.rpc.demo.api.ISayGoodBye;
import com.rocky.universe.rpc.demo.api.ISayHello;
import com.rocky.universe.rpc.demo.api.Person;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rocky on 17/11/21.
 */
public class ClientMain {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);
    public static void main(String[] args) throws TException, InterruptedException {
        ThriftClient<ISayHello.Iface> helloClient = new ThriftClient<>("test", "default", ISayHello.Iface.class, "192.168.60.40:2181");
        ThriftClient<ISayGoodBye.Iface> goodByeClient = new ThriftClient<>("test", "default", ISayGoodBye.Iface.class, "192.168.60.40:2181");
        helloClient.start();
        goodByeClient.start();
        ISayHello.Iface helloRpc = helloClient.getThriftClient();
        ISayGoodBye.Iface goodByeRpc = goodByeClient.getThriftClient();
        while (true) {
            String hello = helloRpc.sayHello(new Person("rocky", 18));
            LOGGER.info(hello);
            String goodBye = goodByeRpc.sayGoodBye(new Person("rocky", 18));
            LOGGER.info(goodBye);
            Thread.sleep(2000L);
        }
    }
}
