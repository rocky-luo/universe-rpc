import com.rocky.universe.rpc.client.ThriftClient;
import com.rocky.universe.rpc.demo.api.ISayGoodBye;
import com.rocky.universe.rpc.demo.api.ISayHello;
import com.rocky.universe.rpc.demo.api.Person;
import org.apache.thrift.TException;

/**
 * Created by rocky on 17/11/21.
 */
public class ClientMain {
    public static void main(String[] args) throws TException, InterruptedException {
        ThriftClient<ISayHello.Iface> helloClient = new ThriftClient<>("test", "default", ISayHello.Iface.class, "192.168.60.40:2181");
        ThriftClient<ISayGoodBye.Iface> goodByeClient = new ThriftClient<>("test", "default", ISayGoodBye.Iface.class, "192.168.60.40:2181");
        helloClient.start();
        goodByeClient.start();
        ISayHello.Iface helloRpc = helloClient.getThriftClient();
        ISayGoodBye.Iface goodByeRpc = goodByeClient.getThriftClient();
        while (true) {
            Thread.sleep(2000L);
            String hello = helloRpc.sayHello(new Person("rocky", 18));
            System.out.println(hello);
            String goodBye = goodByeRpc.sayGoodBye(new Person("rocky", 18));
            System.out.println(goodBye);
        }
    }
}
