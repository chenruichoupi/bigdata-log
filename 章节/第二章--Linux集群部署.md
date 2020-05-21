## 第二章 Linux集群部署
#### 克隆三台虚拟机，每台给2G内存
### 开始配置Linux
#### 1) 修改ip地址；
根据我的配置设置为分别为192.168.1.151,192.168.1.152,192.168.1.153
#### 2) 关闭防火墙：
查看防火墙状态：sudo service iptables status

关闭防火墙：sudo vi /etc/sysconfig/selinux -> disabled
#### 3) 打开或者关闭防火墙：
sudo service iptables start

sudo service iptables stop
#### 4) 卸载 linux 自带的 jdk：
sudo rpm -qa|grep java

sudo rpm -e --nodeps java-1.6.0-openjdk-1.6.0.0-1.50.1.11.5.el6_3.x86_64

tzdata-java-2012j-1.el6.noarch java-1.7.0-openjdk-1.7.0.9-2.3.4.1.el6_3.x86_64
#### 5) 创建用户:
 大数据项目开发中，一般不直接使用root用户，需要我们创建新的用户来操作，比如kfk。 

a）创建用户命令：adduser kfk 

b）设置用户密码命令：passwd kfk
#### 6) 修改主机名:
a）查看主机名命令：hostname

b）修改主机名称 vi /etc/sysconfig/network 

NETWORKING=yes 

HOSTNAME=bigdata-pro01.kfk.com
#### 7) 在/opt/下创建 tools,datas,softwares,modules:

sudo mkdir /opt/softwares

sudo mkdir /opt/modules

sudo mkdir /opt/tools

sudo mkdir /opt/datas

删除/opt/下的 rh 文件   sudo rm -rf ./rh
#### 8) 将文件目录改为 kfk 目录：
sudo chown -R kfk:kfk /opt/*
#### 9) 用 filezilla软件 传 jdk1.8 版本到/opt/software 目录下：
#### 10) 将 softwares 下所有文件改为可执行的:
cd softwares/

chmod u+x /opt/softwares/*
#### 11) 解压 jdk 到 modules 目录下：
tar -zxf jdk压缩包.tar.gz -C /opt/modules/
#### 12) 配置 java 环境变量：
sudo vi /etc/profile

加入如下两行：
export JAVA_HOME=/opt/modules/jdk1.7.0_67

export PATH=$PATH:$JAVA_HOME/bin

环境生效：
source /etc/profile

java -version 显示所安装的java版本即为成功
#### 13) 克隆虚拟机到2，3台机器，克隆完毕按照同样的方法依次配置主机 2， 3 的网卡，主机名，防火墙
#### 14) 虚拟机快照，以便快速退回旧版本中。

