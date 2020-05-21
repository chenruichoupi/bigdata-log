# 第六章 Yarn高可用配置
![](https://img-blog.csdnimg.cn/20200518075812419.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
ResourceManager HA 由一对Active，Standby结点构成，通过RMStateStore存储内部数据和主要应用的数据及标记。目前支持的可替代的RMStateStore实现有：基于内存的MemoryRMStateStore，基于文件系统的FileSystemRMStateStore，及基于zookeeper的ZKRMStateStore。 ResourceManager HA的架构模式同NameNode HA的架构模式基本一致，数据共享由RMStateStore，而ZKFC成为 ResourceManager进程的一个服务，非独立存在。
### 1）修改mapred-site.xml配置文件
    <configuration>
		<property>
			<name>mapreduce.framework.name</name>
			<value>yarn</value>
		</property>
	</configuration>
### 2）修改yarn-site.xml配置文件
    <configuration>
		<property>
			<name>yarn.resourcemanager.cluster-id</name>
			<value>rs</value>
		</property>
		<property>
			<name>yarn.resourcemanager.ha.rm-ids</name>
			<value>rm1,rm2</value>
		</property>
		<property>
			<name>yarn.resourcemanager.hostname.rm1</name>
			<value>bigdata-pro01.kfk.com</value>
		</property>
		<property>
			<name>yarn.resourcemanager.hostname.rm2</name>
			<value>bigdata-pro02.kfk.com</value>
		</property>
		<property>
			<name>yarn.resourcemanager.zk.state-store.address</name>
			<value>bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,
					bigdata-pro03.kfk.com:2181</value>
		</property>
		<property>
			<name>yarn.resourcemanager.zk-address</name>
			<value>bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,
					bigdata-pro03.kfk.com:2181</value>
		</property>
		<property>
			<name>yarn.resourcemanager.recovery.enabled</name>
			<value>true</value>
		</property>
		<property>
			<name>yarn.resourcemanager.ha.enabled</name>
			<value>true</value>
		</property>
		<property>
			<name>yarn.nodemanager.aux-services</name>
			<value>mapreduce_shuffle</value>
		</property>
		<property>
			<name>yarn.nodemanager.aux-services.mapreduce_shuffle.class</name>
			<value>org.apache.hadoop.mapred.ShuffleHandler</value>
		</property>
	</configuration>
### 3）将修改的配置分发到其他节点
scp yarn-site.xml bigdata-pro02.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/

scp yarn-site.xml bigdata-pro03.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/

scp mapred-site.xml bigdata-pro02.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/

scp mapred-site.xml bigdata-pro03.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/
### 4) YARN-HA服务启动及自动故障转移测试
a）在rm1节点上启动yarn服务

sbin/start-yarn.sh	

b）在rm2节点上启动ResourceManager服务

sbin/yarn-daemon.sh start resourcemanager

c）查看yarn的web界面

http://bigdata-pro01.kfk.com:8088

http://bigdata-pro02.kfk.com:8088

d）查看ResourceManager主备节点状态

bigdata-pro01.kfk.com节点上执行

bin/yarn rmadmin -getServiceState rm1

bigdata-pro02.kfk.com节点上执行

bin/yarn rmadmin -getServiceState rm2

e）hadoop集群测试WordCount运行

bin/yarn jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.5.0.jar wordcount /user/kfk/data/wc.input 
