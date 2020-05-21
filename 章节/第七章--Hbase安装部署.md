# Hbase安装部署
HBase是一个高可靠、高性能、面向列、可伸缩的分布式存储系统，利用Hbase技术可在廉价PC Server上搭建 大规模结构化存储集群。
HBase 是Google Bigtable 的开源实现，与Google Bigtable 利用GFS作为其文件存储系统类似， HBase 利用Hadoop HDFS 作为其文件存储系统；Google 运行MapReduce 来处理Bigtable中的海量数据， HBase 同样利用Hadoop MapReduce来处理HBase中的海量数据；Google Bigtable 利用Chubby作为协同服务， HBase 利用Zookeeper作为对应。
![](https://img-blog.csdnimg.cn/20200518101426289.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
## 下载 
这里选择下载cdh版本的hbase-1.0.0-cdh5.4.0.tar.gz，然后上传至bigdata-pro01.kfk.com节点/opt/softwares/目录下

解压hbase:

tar -zxf hbase-1.0.0-cdh5.4.0.tar.gz -C /opt/modules/
## 配置hbase-env.sh
export JAVA_HOME=/opt/modules/你的jdk版本

export HBASE_MANAGES_ZK=false
## hbase-site.xml
    <configuration>
		<property>
    		<name>hbase.rootdir</name>
    		<value>hdfs://ns/hbase</value>
		</property>
		<property>
    		<name>hbase.cluster.distributed</name>
    		<value>true</value>
		</property>
		<property>
			<name>hbase.zookeeper.quorum</name>
			<value>bigdata-pro01.kfk.com,bigdata-pro02.kfk.com,
			bigdata-pro03.kfk.com</value>
		</property>
	</configuration>
## 配置regionservers
bigdata-pro01.kfk.com

bigdata-pro02.kfk.com

bigdata-pro03.kfk.com
## 配置backup-masters
bigdata-pro02.kfk.com
## 将hbase配置分发到各个节点
scp -r hbase-1.0.0-cdh5.4.0 bigdata-pro02.kfk.com:/opt/modules/

scp -r hbase-1.0.0-cdh5.4.0 bigdata-pro03.kfk.com:/opt/modules/
## 启动HBase服务
### 1）各个节点启动Zookeeper
zkServer.sh start
### 2）主节点启动HDFS
bin/start-dfs.sh
### 3）启动HBase
bin/start-hbase.sh
### 4）查看HBase Web界面
bigdata-pro01.kfk.com:60010/

如果各个节点启动正常，那么HBase就搭建完毕。

## 通过shell测试数据库
### 1）选择主节点进入HBase目录，启动hbase-shell
cd hbase-1.0.0-cdh5.4.0

bin/hbase-shell
### 2）查看所有表命令
list
### 3）使用help帮助命令
help
### 4）创建表
create 'test','info'
### 5）添加数据
put 'test','0001','info:userName','laocao'
### 6）全表扫描数据
scan 'test'
### 7）查看表结构
describe 'test'
### 8）删除表
disable 'test'

drop 'test'
## 下载数据源 
[http://www.sogou.com/labs/resource/q.php](http://www.sogou.com/labs/resource/q.php "搜狗实验室")

选择精简版

HBase上创建表：
create 'weblogs','info'

