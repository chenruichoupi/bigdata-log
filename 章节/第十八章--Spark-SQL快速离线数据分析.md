# 第十八章 Spark SQL快速离线数据分析
Spark SQL是Spark核心功能的一部分，是在2014年4月份Spark1.0版本时发布的。

Spark SQL可以直接运行SQL或者HiveQL语句。

BI工具通过JDBC连接SparkSQL查询数据。

Spark SQL支持Python、Scala、Java和R语言。

Spark SQL不仅仅是SQL。Spark SQL远远比SQL要强大。
## Spark SQL 与Hive集成（spark-shell）
##### 我的hive之前放在第三台机器，mysql放在第二=一台机器，spark放在第二台机器。
####1) 将hive的配置文件hive-site.xml拷贝到spark conf目录。
####2) 配置spark core 目录下的hive-site.xml文件，添加metastore的url配置:
vi hive-site.xml

    <property>
		<name>hive.metastore.uris</name>
		<value>thrift://bigdata-pro03.kfk.com:9083</value>
	</property>
####3) 拷贝hive中的mysql jar包到spark的jar目录下:
我是从第三台机器发过去的。
## 启动服务
####1) 第一台机器启动mysql：
sudo service mysqld status

sudo service mysqld start
####2) 启动hive:
    bin/hive

	show databases;

	create database kfk;

	create table if not exists test(userid string,username string)ROW FORMAT DELIMITED FIELDS TERMINATED BY ' ' STORED AS textfile;

	load data local inpath "/opt/datas/test.txt" into table test;
本地kfk.txt文件:
    more /opt/datas/test.txt

	0001 spark
	0002 hive
	0003 hbase
	0004 hadoop
#### 3) 启动spark-shell
    bin/spark-shell
	spark.sql("select * from kfk.test").show

	0001 spark
	0002 hive
	0003 hbase
	0004 hadoop
![](https://img-blog.csdnimg.cn/20200519111406585.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
这样就可以在sparl-shell模式下去处理hive中的数据了。
##同样可以启动sparl-sql处理数据
##spark向mysql写数据：
先确认mysql里有test数据库，没有新建一个。
然后在spark-shell里输入以下命令：

    import java.util.Properties
	val df = spark.sql("select * from kfk.test")
	val pro = new Properties()
	pro.setProperty("driver","com.mysql.jdbc.Driver")
	df.write.jdbc("jdbc:mysql://bigdata-pro01.kfk.com/test?user=root&password=123456","spark1",pro);
现在就可以去mysql的test数据库里查看写入的数据啦。
![](https://img-blog.csdnimg.cn/20200519111758139.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

