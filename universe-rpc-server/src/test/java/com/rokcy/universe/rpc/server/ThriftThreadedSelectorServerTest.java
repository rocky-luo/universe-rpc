package com.rokcy.universe.rpc.server;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.rokcy.universe.rpc.server.thrift.ISayGoodBye;
import com.rokcy.universe.rpc.server.thrift.ISayHello;
import com.rokcy.universe.rpc.server.thrift.Person;
import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by rocky on 17/10/20.
 */
public class ThriftThreadedSelectorServerTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftThreadedSelectorServerTest.class);
    String zkConnect;
    @Before
    public void init(){
        zkConnect = "192.168.60.40:2181";
    }
    @Test
    public void singleHandlerStartTest() throws TException, InterruptedException {
        List<Object> handlers = Lists.newArrayList((Object) new SayHelloHandler());
        Server server = new ThriftThreadedSelectorServer(handlers, "test", "default", 8421, zkConnect, 2);
        server.start();
        // 服务器所在的IP和端口
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8421));
        TProtocol protocol = new TCompactProtocol(transport);

        // 准备调用参数
        ISayHello.Iface client = new ISayHello.Client(protocol);

        // 准备传输
        transport.open();
        // 正式调用接口
        Person person = new Person("Jack", 18);
        String result = client.sayHello(person);
        String expect = new SayHelloHandler().sayHello(person);
        Assert.assertEquals(expect, result);
        // 一定要记住关闭
        transport.close();
    }

    @Test
    public void multiHandlerStartTest() throws InterruptedException, TException {
        List<Object> handlers = Lists.newArrayList((Object) new SayHelloHandler(), (Object) new SayGoodByeHandler());
        Server server = new ThriftThreadedSelectorServer(handlers, "test", "default", 8421, zkConnect, 2);
        server.start();
        // 服务器所在的IP和端口
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8421));
        TProtocol protocol = new TCompactProtocol(transport);
        TMultiplexedProtocol helloProtocol = new TMultiplexedProtocol(protocol, ISayHello.Iface.class.getName());
        TMultiplexedProtocol goodbyeProtocol = new TMultiplexedProtocol(protocol, ISayGoodBye.Iface.class.getName());


        // 准备调用参数
        ISayHello.Iface helloClient = new ISayHello.Client(helloProtocol);
        ISayGoodBye.Iface goodByeClient = new ISayGoodBye.Client(goodbyeProtocol);
        // 准备传输
        transport.open();
        // 正式调用接口
        Person person = new Person("Jack", 18);
        String helloResult = helloClient.sayHello(person);
        String helloExpect = new SayHelloHandler().sayHello(person);
        Assert.assertEquals(helloExpect, helloResult);

        String goodbyeResult = goodByeClient.sayGoodBye(person);
        String goodbyeExpect = new SayGoodByeHandler().sayGoodBye(person);
        Assert.assertEquals(goodbyeExpect, goodbyeResult);
        // 一定要记住关闭
        transport.close();
    }


    @Test
    public void clientTest() throws TException {
        // 服务器所在的IP和端口
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8421));
        TProtocol protocol = new TCompactProtocol(transport);

        // 准备调用参数
        ISayHello.Iface client = new ISayHello.Client(protocol);

        // 准备传输
        transport.open();
        // 正式调用接口
        Person person = new Person("Jack", 18);
        String result = client.sayHello(person);
        String expect = new SayHelloHandler().sayHello(person);
        Assert.assertEquals(expect, result);
        // 一定要记住关闭
        transport.close();
    }

    @Test
    public void simpleServer() throws TTransportException {
        TNonblockingServerTransport serverSocket=new TNonblockingServerSocket(8890);
        TThreadedSelectorServer.Args serverParams=new TThreadedSelectorServer.Args(serverSocket);
        serverParams.protocolFactory(new TCompactProtocol.Factory());
        TProcessor processor = new ISayHello.Processor<ISayHello.Iface>(new SayHelloHandler());
        serverParams.processor(processor);
        serverParams.executorService(Executors.newFixedThreadPool(3));
        TServer server=new TThreadedSelectorServer(serverParams); //简单的单线程服务模型，常用于测试
        server.serve();
    }

    @Test
    public void simpleClient() throws TException {
        TTransport transport = new TFramedTransport(new TSocket("localhost", 8890));
        TProtocol protocol = new TCompactProtocol(transport);
        ISayHello.Client client = new ISayHello.Client(protocol);
        transport.open();
        Person person = new Person("jack", 18);
        String respone = client.sayHello(person);
    }

    @Test
    public void simpleServer2() throws TTransportException {
        TNonblockingServerTransport serverSocket=new TNonblockingServerSocket(8891);
        TThreadedSelectorServer.Args serverParams=new TThreadedSelectorServer.Args(serverSocket);
        serverParams.protocolFactory(new TCompactProtocol.Factory());
        TProcessor processor = new ISayHello.Processor<ISayHello.Iface>(new SayHelloHandler());
        TProcessor processor1 = new ISayGoodBye.Processor<ISayGoodBye.Iface>(new SayGoodByeHandler());
        TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
        multiplexedProcessor.registerProcessor("1", processor);
        multiplexedProcessor.registerProcessor("2", processor1);
        serverParams.processor(multiplexedProcessor);
        TServer server=new TThreadedSelectorServer(serverParams);
        server.serve();
    }
    @Test
    public void simpleClient2() throws TException {
        TTransport transport = new TFramedTransport(new TSocket("localhost", 8891));
        TProtocol protocol = new TCompactProtocol(transport);
        TMultiplexedProtocol tMultiplexedProtocol = new TMultiplexedProtocol(protocol, "1");
        ISayHello.Client client = new ISayHello.Client(tMultiplexedProtocol);
        transport.open();
        Person person = new Person("jack", 19);
        String respone = client.sayHello(person);
    }

    @Test
    public void serverStartTest() throws InterruptedException {
        List<Object> handlers = Lists.newArrayList((Object) new SayHelloHandler());
        Server server = new ThriftThreadedSelectorServer(handlers, "test", "default", 8421, zkConnect, 2);
        server.start();
        while (true);
    }

//    @Test
//    public void servingTest() throws TTransportException, InterruptedException {
//        TNonblockingServerTransport serverSocket=new TNonblockingServerSocket(8890);
//        TThreadedSelectorServer.Args serverParams=new TThreadedSelectorServer.Args(serverSocket);
//        serverParams.protocolFactory(new TCompactProtocol.Factory());
//        TProcessor processor = new ISayHello.Processor<ISayHello.Iface>(new SayHelloHandler());
//        serverParams.processor(processor);
//        serverParams.executorService(Executors.newFixedThreadPool(3));
//        TServer server=new TThreadedSelectorServer(serverParams); //简单的单线程服务模型，常用于测试
//        Runnable start = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("service ready to start!");
//                server.serve();
//            }
//        };
//        new Thread(start).start();
//        int count = 0;
//        while (count < 500) {
//            String isStart = server.isServing()?"start" : "NOT start";
//            System.out.println("the service status is " + isStart);
//            count++;
//        }
//    }

    @Test
    public void stopTest(){
        List<Object> handlers = Lists.newArrayList((Object) new SayHelloHandler());
        Server server = new ThriftThreadedSelectorServer(handlers, "test", "default", 8421, zkConnect, 2);
        server.start();
        System.out.println("started");
        server.stop();
        System.out.println("stopped");
    }
}