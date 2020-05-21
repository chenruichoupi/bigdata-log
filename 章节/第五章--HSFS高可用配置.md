# 第五章 Hadoop2.x HA部署
## HDFS-HA架构原理介绍:
hadoop2.x之后，Clouera提出了QJM/Qurom Journal Manager，这是一个基于Paxos算法实现的HDFS HA方案，它给出了一种较好的解决思路和方案,示意图如下：
![](https://img-blog.csdnimg.cn/20200518073435409.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

1）基本原理就是用2N+1台 JN 存储EditLog，每次写数据操作有大多数（>=N+1）返回成功时即认为该次写成功，数据不会丢失了。当然这个算法所能容忍的是最多有N台机器挂掉，如果多于N台挂掉，这个算法就失效了。这个原理是基于Paxos算法

2）在HA架构里面SecondaryNameNode这个冷备角色已经不存在了，为了保持standby NN时时的与主Active NN的元数据保持一致，他们之间交互通过一系列守护的轻量级进程JournalNode

3）任何修改操作在 Active NN上执行时，JN进程同时也会记录修改log到至少半数以上的JN中，这时 Standby NN 监测到JN 里面的同步log发生变化了会读取 JN 里面的修改log，然后同步到自己的的目录镜像树里面，如下图：
![](https://img-blog.csdnimg.cn/20200518073631721.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

当发生故障时，Active的 NN 挂掉后，Standby NN 会在它成为Active NN 前，读取所有的JN里面的修改日志，这样就能高可靠的保证与挂掉的NN的目录镜像树一致，然后无缝的接替它的职责，维护来自客户端请求，从而达到一个高可用的目的。
## HDFS-HA 详细配置
### 1）修改hdfs-site.xml配置文件

vi hdfs-site.xml

    <configuration>
		<property>
	        <name>dfs.replication</name>
			<value>3</value>
	    </property>
		<property>
	        <name>dfs.permissions</name>
	        <value>false</value>
	    </property>
		<property>
	        <name>dfs.permissions.enabled</name>
	        <value>false</value>
	    </property>
		<property>
            <name>dfs.nameservices</name>
            <value>ns</value>
        </property>
		<property>
            <name>dfs.ha.namenodes.ns</name>
            <value>nn1,nn2</value>
        </property>
        <property>
            <name>dfs.namenode.rpc-address.ns.nn1</name>
            <value>bigdata-pro01.kfk.com:8020</value>
        </property>
		<property>
            <name>dfs.namenode.rpc-address.ns.nn2</name>
            <value>bigdata-pro02.kfk.com:8020</value>
        </property>
        <property>
            <name>dfs.namenode.http-address.ns.nn1</name>
            <value>bigdata-pro01.kfk.com:50070</value>
        </property>
	
        <property>
            <name>dfs.namenode.http-address.ns.nn2</name>
            <value>bigdata-pro02.kfk.com:50070</value>
        </property>
	
        <property>
            <name>dfs.namenode.shared.edits.dir</name>
            <value>qjournal://bigdata-pro01.kfk.com:8485;bigdata-pro02.kfk.com:8485;bigdata-pro03.kfk.com:8485/ns</value>
        </property>
		<property>
            <name>dfs.journalnode.edits.dir</name>
            <value>/opt/modules/hadoop-2.5.0/data/jn</value>
        </property>
		<property>
        	<name>dfs.client.failover.proxy.provider.ns</name>
            <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
        </property>
		<property>
            <name>dfs.ha.automatic-failover.enabled</name>
            <value>true</value>
        </property>      
		<property>
			<name>dfs.ha.fencing.methods</name>
			<value>sshfence</value>
		</property>
        <property>
            <name>dfs.ha.fencing.ssh.private-key-files</name>
            <value>/home/kfk/.ssh/id_rsa</value>
        </property>
	</configuration>
### 2）修改core-site.xml配置文件

    <configuration>
		<property>
			<name>fs.defaultFS</name>
			<value>hdfs://ns</value>
		</property>
		<property>
			<name>hadoop.http.staticuser.user</name>
			<value>kfk</value>
		</property>
		<property>
			<name>hadoop.tmp.dir</name>
			<value>/opt/modules/hadoop-2.5.0/data/tmp</value>
		</property>
		<property>
			<name>dfs.namenode.name.dir</name>
			<value>file://${hadoop.tmp.dir}/dfs/name</value>
		</property>
		<property>
			<name>ha.zookeeper.quorum</name>
			<value>bigdata-pro01.kfk.com:2181,bigdata-pro02.kfk.com:2181,
					bigdata-pro03.kfk.com:2181</value>
		</property>
	</configuration>
### 3）将修改的配置分发到其他节点
scp hdfs-site.xml bigdata-pro02.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/

scp hdfs-site.xml bigdata-pro03.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/

scp core-site.xml bigdata-pro02.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/

scp core-site.xml bigdata-pro03.kfk.com:/opt/modules/hadoop-2.6.0/etc/hadoop/
### 4) HDFS-HA 服务启动及自动故障转移测试
a）启动所有节点上面的Zookeeper进程

zkServer.sh start

b）启动所有节点上面的journalnode进程

sbin/hadoop-daemon.sh start journalnode

c）在[nn1]上，对namenode进行格式化，并启动

bin/hdfs namenode -format

bin/hdfs zkfc -formatZK

bin/hdfs namenode

d）在[nn2]上，同步nn1元数据信息

bin/hdfs namenode -bootstrapStandby

e）nn2同步完数据后，在nn1上，按下ctrl+c来结束namenode进程。然后关闭所有节点上面的journalnode进程

sbin/hadoop-daemon.sh stop journalnode

f）一键启动hdfs所有相关进程

sbin/start-dfs.sh

hdfs启动之后，kill其中Active状态的namenode，检查另外一个NameNode是否会自动切换为Active状态。同时通过命令上传文件至hdfs，检查hdfs是否可用。





