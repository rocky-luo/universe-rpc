package com.rocky.universe.rpc.demo.server;

import com.rocky.universe.rpc.demo.api.ISayHello;
import com.rocky.universe.rpc.demo.api.Person;
import org.apache.thrift.TException;

import java.util.UUID;

/**
 * Created by rocky on 17/10/20.
 */
public class SayHelloHandler implements ISayHello.Iface{
    public String sayHello(Person person) throws TException {
        return String.format("[%s]: hello, %s(%s)", Server.ID, person.getName(), person.getAge());
    }
}
