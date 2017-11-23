package com.rocky.universe.rpc.demo.server;

import com.rocky.universe.rpc.demo.api.ISayGoodBye;
import com.rocky.universe.rpc.demo.api.Person;
import org.apache.thrift.TException;

/**
 * Created by rocky on 17/10/20.
 */
public class SayGoodByeHandler implements ISayGoodBye.Iface{
    public String sayGoodBye(Person person) throws TException {
        return String.format("%s: goodbye, %s(%s)", Server.ID, person.getName(), person.getAge());
    }
}
