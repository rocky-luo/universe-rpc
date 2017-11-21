package com.rocky.universe.rpc.common.thrift;

import com.rocky.universe.rpc.common.thrift.thrift.ISayHello;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import static org.junit.Assert.*;

/**
 * Created by rocky on 17/11/20.
 */
public class ThriftHelperTest {

    Object handler;
    @Before
    public void init() {
        handler = new SayHelloHandler();
    }

    @Test
    public void getThriftServiceIface() throws Exception {
        Class clazz = ThriftHelper.getThriftServiceIface(handler.getClass());
        Assert.assertEquals(ISayHello.Iface.class, clazz);
    }

    @Test
    public void getThriftServiceClassByIfaceClass() throws Exception {
        Class clazz = ThriftHelper.getThriftServiceClassByIfaceClass(ISayHello.Iface.class);
        Assert.assertEquals(ISayHello.class, clazz);

    }

    @Test
    public void getThriftServiceClassByProcessClass() throws Exception {
        Class clazz = ThriftHelper.getThriftServiceClassByProcessClass(ISayHello.Processor.class);
        Assert.assertEquals(ISayHello.class, clazz);
    }

    @Test
    public void getIfaceClassByThriftServiceClass() throws Exception {
        Class clazz = ThriftHelper.getIfaceClassByThriftServiceClass(ISayHello.class);
        Assert.assertEquals(ISayHello.Iface.class, clazz);
    }

    @Test
    public void getTProcessorClass() throws Exception {
        Class clazz = ThriftHelper.getTProcessorClass(ISayHello.class);
        Assert.assertEquals(ISayHello.Processor.class, clazz);
    }

    @Test
    public void getClientClass(){
        Class clazz = ThriftHelper.getClientClass(ISayHello.class);
        Assert.assertEquals(ISayHello.Client.class, clazz);
    }

}