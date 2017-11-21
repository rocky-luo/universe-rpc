package com.rocky.universe.rpc.demo.server;

import com.rocky.universe.rpc.demo.api.ISayHello;
import com.rocky.universe.rpc.demo.api.Person;
import org.apache.thrift.TException;

/**
 * Created by rocky on 17/10/20.
 */
public class SayHelloHandler implements ISayHello.Iface{
    public String sayHello(Person person) throws TException {
        return "hello " + person.getName() + " who is " + person.getAge();
    }
}
