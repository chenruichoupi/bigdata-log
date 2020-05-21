# 第十五章 Spark本地模式安装部署
Spark 是一个用来实现快速而通用的集群计算的平台。
在速度方面， Spark 扩展了广泛使用的 MapReduce 计算模型，而且高效地支持更多计算模式，包括交互式查询和流处理。 在处理大规模数据集时，速度是非常重要的。速度快就意味着我们可以进行交互式的数据操作， 否则我们每次操作就需要等待数分钟甚至数小时。
Spark 的一个主要特点就是能够在内存中进行计算， 因而更快。不过即使是必须在磁盘上进行的复杂计算， Spark 依然比 MapReduce 更加高效。
![](https://img-blog.csdnimg.cn/20200518115709679.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDQwODY5MA==,size_16,color_FFFFFF,t_70)
## Spark2.2源码下载到bigdata-pro02.kfk.com节点的/opt/softwares/目录下。
#### 解压

tar -zxf spark-2.2.0.tgz -C /opt/modules/
##### spark2.2编译所需要的环境：Maven3.3.9和Java8

## 下载apache-maven-3.3.9-bin.tar.gz

[http://maven.apache.org/download.cgi](http://maven.apache.org/download.cgi)
## 解压maven
tar -zxf apache-maven-3.3.9-bin.tar.gz -C /opt/modules/

### 配置MAVEN_HOME
    sudo vi /etc/profile
	export MAVEN_HOME=/opt/modules/apache-maven-3.3.9
	export PATH=$PATH:$MAVEN_HOME/bin
	export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=1024M -XX:ReservedCodeCacheSize=1024M"
### 编辑退出之后，使之生效
source /etc/profile
### 查看maven版本
mvn -version
## 下载scala-2.11.8
[http://www.scala-lang.org/download/](http://www.scala-lang.org/download/)

tar -zxf scala-2.11.8.tgz -C /opt/modules/
## 配置环境变量
vi /etc/profile

export SCALA_HOME=/opt/modules/scala-2.11.8

export PATH=$PATH:$SCALA_HOME/bin
### 编辑退出之后，使之生效
source /etc/profile
## WEB页面
bigdata-pro02.kfk.com:4040






