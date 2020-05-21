# 第二十章 Structrued Streaming处理业务数据
## Structrued Streaming编程模型
在第三台机器打开metestore:

	bin/hive --service metastore
第二台机器打开nc:

	nc -lk 9999
第二台机器新开一个窗口打开spark-shell:

	bin/spark-shell
spark-shell下输入:

	:paste
	import org.apache.spark.sql.functions._
	import org.apache.spark.sql.SparkSession
	import spark.implicits._
	val lines = spark.readStream
	  .format("socket")
	  .option("host", "bigdata-pro02.kfk.com")
	  .option("port", 9999)
	  .load()
	val words = lines.as[String].flatMap(_.split(" "))
	val wordCounts = words.groupBy("value").count()
	val query = wordCounts.writeStream
	  .outputMode("complete")
	  .format("console")
	  .start() 
按ctrl+d结束输入

nc窗口下输入数据，回到spark-shell下观察统计结果。可以发现每次结果的统计都是完整的(complete）,即使在现在时间段下某些单词不增长，结果也会显示出来:

![](https://img-blog.csdnimg.cn/20200521093456719.png)
 ![](https://img-blog.csdnimg.cn/20200521093550804.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
结果如下:
![](https://img-blog.csdnimg.cn/20200521093631841.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
####如果把complete参数改为update，结果会怎么样呢，跟上面一样的步骤，我们可以观察到updata下只会记录有变化的数据，而数量不变化的数据是不会打印到控制台的。
结果如下：
![](https://img-blog.csdnimg.cn/20200521093843910.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)


我们的业务分析其实就是基于update模式下的数据统计。
## Stuctured Streaming 与Kafka集成（一）
以下程序模拟从kafka生产数据，在idea工具中查看统计结果。
idea程序中新建scala object,输入以下程序：

    import org.apache.spark.sql.SparkSession
    val spark = SparkSession.builder().master("local[2]").appName("Streaming").getOrCreate()
    import spark.implicits._
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "bigdata-pro01.kfk.com:9092")
      .option("subscribe", "weblogs")
      .load()
    val lines = df.selectExpr("CAST(value AS STRING)").as[(String)]
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.groupBy("value").count()
    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()
    query.awaitTermination()
在机器上开启机器一的kafka服务和生产者服务：

	bin/kafka-server-start.sh -daemon config/server.properties
	bin/kafka-console-producer.sh --broker-list bigdata-pro01.kfk.com:9092 --topic weblogs
然后输入单词：
![](https://img-blog.csdnimg.cn/20200521194125317.png)
在idea控制台可得到单词的统计结果：

![](https://img-blog.csdnimg.cn/20200521194214514.png)
## Stuctured Streaming 与Kafka集成（二）
这次我们在spark-shell上跑数据看一下，kafka流程与上述一样，不过为了让spark-shell跑起来，组要添加一些jar包，前两个jar包在kafka/lib目录下，后两个jar包在本地maven仓库下，将这四个jar包拷贝进spark/jars目录下就可以了。
![](https://img-blog.csdnimg.cn/20200521214231510.png)

然后打开spark-shell本地模式，

	bin/spark-shell --master local[2]
	:paste
	val df = spark
	  .readStream
	  .format("kafka")
	  .option("kafka.bootstrap.servers", "bigdata-pro01.kfk.com:9092")
	  .option("subscribe", "weblogs")
	  .load()
	import spark.implicits._
	val lines = df.selectExpr("CAST(value AS STRING)").as[(String)]
	val words = lines.flatMap(_.split(" "))
	val wordCounts = words.groupBy("value").count()
	val query = wordCounts.writeStream
	  .outputMode("complete")
	  .format("console")
	  .start()
	query.awaitTermination()

回车然后ctrl+d结束输入。然后在kafka生产者端写入数据，spark-shell命令行下就会count出数据。你也可以将模式从complete换为update模式观察数据计算结果。

![](https://img-blog.csdnimg.cn/20200521214635141.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)

以上两个就是structuredStreaming从kafka拿数据的步骤啦。下面介绍structuredStreaming写入mysql的编程模型。
