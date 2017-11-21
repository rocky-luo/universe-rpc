package com.rocky.universe.rpc.common.thrift;

import com.rocky.universe.rpc.common.thrift.thrift.ISayHello;
import com.rocky.universe.rpc.common.thrift.thrift.Person;
import org.apache.thrift.TException;

/**
 * Created by rocky on 17/10/20.
 */
public class SayHelloHandler implements ISayHello.Iface{
    public String sayHello(Person person) throws TException {
        return "hello " + person.getName() + " who is " + person.getAge();
    }
}
