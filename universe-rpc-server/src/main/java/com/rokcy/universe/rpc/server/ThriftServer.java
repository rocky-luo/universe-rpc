package com.rokcy.universe.rpc.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Created by rocky on 17/10/17.
 */
public abstract class ThriftServer extends AbstractServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftServer.class);
    private TProcessor tProcessor;

    public ThriftServer(List<Object> handlers, String app, String group, int port, String zkConnect) {
        super(interfaceMap(handlers), app, group, port, zkConnect);
        tProcessor = createTProcessor(handlers);
    }

    public void start() {
        synchronized (this) {
            if (!tServer().isServing()) {
                Runnable serve = new Runnable() {
                    @Override
                    public void run() {
                        tServer().serve();
                    }
                };
                new Thread(serve).start();
                while (!tServer().isServing()) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        LOGGER.error("starting serve is interrupted", e);
                    }
                }
                register();
                LOGGER.info("server started!");
            }
        }
    }

    public void stop() {
        synchronized (this) {
            if (tServer().isServing()) {
                unregister();
                tServer().stop();
                while (tServer().isServing()) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        LOGGER.error("stop serve is interrupted", e);
                    }
                }
                LOGGER.info("server stopped!");
            }

        }
    }

    protected abstract TServer tServer();

    protected TProcessor getTProcessor() {
        return tProcessor;
    }

    private static TProcessor createTProcessor(Object handler) {
        Class iface = ThriftHelper.getThriftServiceIface(handler.getClass());
        Class thriftServiceClass = ThriftHelper.getThriftServiceClassByIfaceClass(iface);
        Class<TProcessor> processorClass = ThriftHelper.getTProcessorClass(thriftServiceClass);
        try {
            Constructor<TProcessor> processorConstructor = processorClass.getConstructor(iface);
            TProcessor processor = processorConstructor.newInstance(handler);
            return processor;
        } catch (Exception e) {
            throw new RuntimeException("can not create processor with class " + handler.getClass().getName(), e);
        }
    }

    private static TProcessor createTProcessor(List<Object> handlers) {
        List<TProcessor> tProcessors = Lists.newArrayListWithCapacity(handlers.size());
        for (Object handler : handlers) {
            tProcessors.add(createTProcessor(handler));
        }
        if (tProcessors.size() == 1) {
            return tProcessors.get(0);
        }else {
            return multiTProcessor(tProcessors);
        }
    }

    private static TProcessor multiTProcessor(List<TProcessor> tProcessors) {
        TMultiplexedProcessor multiPro = new TMultiplexedProcessor();
        for (int i = 0; i < tProcessors.size(); i++) {
            TProcessor processor = tProcessors.get(i);
            Class iface = ThriftHelper.getIfaceClassByThriftServiceClass(
                    ThriftHelper.getThriftServiceClassByProcessClass(processor.getClass()));
            multiPro.registerProcessor(iface.getName(), tProcessors.get(i));
        }
        return multiPro;
    }

    private static Map<Class, Object> interfaceMap(List<Object> handlers) {
        Map<Class, Object> interfaceMap = Maps.newHashMapWithExpectedSize(handlers.size());
        for (Object handler : handlers) {
            Class iface = ThriftHelper.getIfaceClassByImpl(handler);
            interfaceMap.put(iface, handler);
        }
        return interfaceMap;
    }

}
