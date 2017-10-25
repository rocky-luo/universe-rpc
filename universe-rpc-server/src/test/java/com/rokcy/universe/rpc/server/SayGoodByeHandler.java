package com.rokcy.universe.rpc.server;

import com.rokcy.universe.rpc.server.thrift.ISayGoodBye;
import com.rokcy.universe.rpc.server.thrift.Person;
import org.apache.thrift.TException;

/**
 * Created by rocky on 17/10/20.
 */
public class SayGoodByeHandler implements ISayGoodBye.Iface{
    public String sayGoodBye(Person person) throws TException {
        return "goodbye" + person.getName() + " who is " + person.getAge();
    }
}
