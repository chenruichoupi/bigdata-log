# 第十六章 Spark standalone和on Yarn模式部署
## standalon模式
#### 这里我只配置了机器二为master和worker
我用版本是spark-2.2.0-bin-hadoop2.6.tgz

环境要求：scala-2.11.12.tgz/java8/hadoop2.6.0.

#####配置 spark-env.sh
    export JAVA_HOME=/opt/modules/jdk1.8.0_191
	export SCALA_HOME=/opt/modules/scala-2.11.12
	
	export HADOOP_CONF_DIR=/opt/modules/hadoop-2.6.0/etc/hadoop
	export SPARK_CONF_DIR=/opt/modules/spark-2.2.0/conf
	export SPARK_MASTER_HOST=bigdata-pro02.kfk.com
	export SPARK_MASTER_PORT=7077
	export SPARK_MASTER_WEBUI_PORT=8080
	export SPARK_WORKER_CORES=1
	export SPARK_WORKER_MEMORY=1g
	export SPARK_WORKER_PORT=7078
	export SPARK_WORKER_WEBUI_PORT=8081
注意这里HADOOP_CONF_DIR在stndalone模式下是不需要的，在yarn模式下需要，这里就直接配置了。
#####配置slaves
    bigdata-pro02.kfk.com
#####进入客户端模式：
    bin/spark-shell --master spark://bigdata-pro02.kfk.com:7077
## on Yar模式
####注意hadoop配置文件中jdk版本是否与当前jdk版本一致
####打开三个节点的hdfs进程，yarn进程
#####打开客户端：
    bin/spark-shell --master yarn --deploy-mode client
这里可能会出现yarn资源不够分配的报错：
![](https://img-blog.csdnimg.cn/20200519085604348.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
这时先关闭各个结点的yarn进程，三个机器上都配置

/opt/modules/hadoop-2.6.0/etc/hadoop/yarn-site.xml

加入两行，确保资源不够的时候不要杀掉进程：

    <property>
		<name>yarn.nodemanager.pmem-check-enabled</name>
		<value>false</value>
	</property>
	<property>
		<name>yarn.nodemanager.vmem-check-enabled</name>
		<value>false</value>
	</property>

####然后再次打开三个机器的yarn进程
再次进入spark客户端就会好使了。

![](https://img-blog.csdnimg.cn/20200519085921368.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

