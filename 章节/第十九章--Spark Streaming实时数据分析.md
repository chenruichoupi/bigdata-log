# 第十九章 Spark Streaming实时数据分析
Spark流是核心Spark API的扩展，支持可伸缩、高吞吐量、容错的实时数据流处理。
![](https://img-blog.csdnimg.cn/20200520153752102.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
## NC服务安装并运行Spark Streaming的wordcount程序
名称：nc

摘要：使用TCP或UDP跨网络连接读取和写入数据。

说明：
nc软件包包含Netcat（程序实际上是nc），这是一个使用TCP或UDP协议跨网络连接读取和写入数据的简单实用程序。Netcat旨在成为可靠的后端工具，可以直接使用或由其他程序和脚本轻松驱动。Netcat还是功能丰富的网络调试和探索工具，因为它可以创建许多不同的连接并具有许多内置功能。

下载地址：[http://rpm.pbone.net/index.php3/stat/4/idpl/15991371/dir/scientific_linux_6/com/](http://rpm.pbone.net/index.php3/stat/4/idpl/15991371/dir/scientific_linux_6/com/)

下载第一个即可，得到 nc-1.84-22.el6.x86_64.rpm 文件，上传到第二台机器的softwares目录下，因为spark机器安在第二台，等下运行sparkstreaming时，不需跨机器读取数据，速度快一些。

修改第二台机器spark中的log4j.properties文件，更改以下三项权限为WARN
![](https://img-blog.csdnimg.cn/20200520154604211.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

在第二台机器的softwares目录下，执行：

    sudo rpm -ivh nc-1.84-22.el6.x86_64.rpm
	which nc 
	nc -lk 9999
打开了nc窗口，然后新开个机器二的窗口，进入spark目录，执行streaming的wordcount程序：

	bin/run-example --master local[2] streaming.NetworkWordCount localhost 9999
在nc窗口下多次敲打以下单词，查看第二个窗口下是否出现统计结果：

	hive hbase
	hive hbase
	java hive

最后出现结果如下:
![](https://img-blog.csdnimg.cn/20200520154902158.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)