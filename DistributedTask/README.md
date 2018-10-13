# DistributedTask

分布式任务计算逻辑JAR包

- jar包可指定main方法的类的全路径（定义在config\MainClass.properties文件中）
- 由NameNode进行调度，自适应地配合计算

## 概述

DistributedTask一定是在DataNode端执行的，因此任务JAR包的代码严重依赖DataNode母体的能力。

当DataNode母体能力不足以支撑DataNode任务的执行时，此时就需要对DataNode的功能进行升级。

同样，JAR包能力也严重依赖NameNode，在适当的时刻，NameNode同样需要升级自己的能力！

可以使用NameNode的一键更新客户端功能，对所有在线的DataNode客户端一键升级！这样就大大降低了统一部署的难度！
