package com.rokcy.universe.rpc.server;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by rocky on 17/10/17.
 */
public class ThriftThreadedSelectorServer extends ThriftServer {
    private int workerSize;
    private TServer tServer;

    public ThriftThreadedSelectorServer(List<Object> handlers, String app, String group, int port, String zkConnect, int workerSize) {
        super(handlers, app, group, port, zkConnect);
        this.workerSize = workerSize;
        this.tServer = createTServer();
    }


    private TServer createTServer() {
        TNonblockingServerTransport transport = null;
        try {
            transport = new TNonblockingServerSocket(getPort());
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);
        args.protocolFactory(new TCompactProtocol.Factory());
        args.processor(getTProcessor());
        args.executorService(Executors.newFixedThreadPool(workerSize));
        return new TThreadedSelectorServer(args);
    }

    protected TServer tServer() {
        return tServer;
    }
}
