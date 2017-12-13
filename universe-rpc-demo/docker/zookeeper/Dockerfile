FROM hub.c.163.com/public/centos:6.5-tools
RUN yum install -y java-1.8.0-openjdk*
RUN yum clean all
WORKDIR /opt
RUN wget http://mirrors.hust.edu.cn/apache/zookeeper/zookeeper-3.4.11/zookeeper-3.4.11.tar.gz
RUN tar xzvf zookeeper-3.4.11.tar.gz
RUN rm -rf zookeeper-3.4.11.tar.gz
RUN cp zookeeper-3.4.11/conf/zoo_sample.cfg zookeeper-3.4.11/conf/zoo.cfg
WORKDIR /
EXPOSE 2181
CMD /opt/zookeeper-3.4.11/bin/zkServer.sh start-foreground