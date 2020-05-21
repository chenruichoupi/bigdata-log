# Kafka安装部署
Kafka是由LinkedIn开发的一个分布式的消息系统，使用Scala编写，它以高水平扩展和高吞吐率而被广泛使用。目前越来越多的开源分布式处理系统如Cloudera、Apache Storm、Spark都支持与Kafka集成。
![](https://img-blog.csdnimg.cn/2020051810292246.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

![](https://img-blog.csdnimg.cn/20200518103850659.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
## 1.下载安装
这里选择下载Apache版本的kafka_2.11-0.9.0.0.tgz ，然后上传至bigdata-pro01.kfk.com节点/opt/softwares/目录下
### 解压Kafka
tar -zxf kafka_2.11-0.9.0.0.tgz  -C /opt/modules/
## 2.Kafka集群配置
### 1）配置server.properties文件
vi kafka_2.11-0.9.0.0

broker.id=0

port=9092

host.name=bigdata-pro01.kfk.com

log.dirs=/opt/modules/kafka_2.11-0.9.0.0/tmp/kafka-logs

zookeeper.connect=bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181
### 2）配置zookeeper.properties文件
vi zookeeper.properties

dataDir=/opt/modules/zookeeper-3.4.5-cdh5.10.0/zkData
### 3）配置consumer.properties文件
zookeeper.connect=bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,bigdata-pro03.kfk.com:2181
### 4）配置producer.properties文件
metadata.broker.list=bigdata-pro01.kfk.com:9092,bigdata-pro02.kfk.com:9092,bigdata-pro03.kfk.com:9092
### 5）Kafka分发到其他节点
scp -r kafka_2.11-0.9.0.0 bigdata-pro02.kfk.com:/opt/modules/

scp -r kafka_2.11-0.9.0.0 bigdata-pro03.kfk.com:/opt/modules/
### 6）修改另外两个节点的server.properties

broker.id=1
host.name=bigdata-pro02.kfk.com

broker.id=2
host.name=bigdata-pro03.kfk.com
## 3.启动Kafka集群并进行测试
### 1）各个节点启动Zookeeper集群
bin/zkServer.sh start
### 2）各个节点启动Kafka集群
bin/kafka-server-start.sh -daemon config/server.properties 
### 3）创建topic
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test --replication-factor 1 --partitions 1
### 4）查看topic列表
bin/kafka-topics.sh --zookeeper localhost:2181 --list
### 5）生产者生成数据
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
### 6）消费者消费数据
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning

