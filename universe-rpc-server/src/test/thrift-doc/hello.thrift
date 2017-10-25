namespace java com.rocky.universe.rpc.server.thrift

struct Person {
    1:string name;
    2:i32 age;
}

service ISayHello {
    string sayHello(1:Person person);
}

service ISayGoodBye {
    string sayGoodBye(1:Person person);
}