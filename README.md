# 分布式唯一ID

使用snowflake算法，算法描述百度有。

## sequence-core

### workerId

工作机器标识分配了10bit，即最多可以有1024个节点。

目前只有一个规则，就是使用 endpoint 标识不同的节点，如 192.168.1.190:8080 与 192.168.1.190:8081 是二个不同的节点

> 在多网卡多地址这种复杂的网络拓扑，获取到的 endpoint 可能会与预期不一样，这里使用 Docker 部署避免这个问题。

## sequence-server

提供序列号服务，建议使用Docker部署。

## sequence-demo

代码使用 feign 声明服务，使用 sequence-server 提供的服务。

## TODO

获取工作机器标识的规则有待丰富

## 版本发布

1、从develop分支创建release分支，并将develop分支设置为下一个版本

```
mvn versions:set -DnextSnapshot=true
```
也可以指定版本号
```
mvn versions:set -DnewVersion=0.1.1-SNAPSHOT
```

确认正确后提交修改
```
mvn versions:commit
```
当然也可以回退版本
```
mvn versions:revert
```

2、当release版本测试通过后就可以发布了，首先切换到release分支，将版本号从SNAPSHOT更改为RELEASE

```
mvn versions:set -DnewVersion=0.1.RELEASE
```

提交修改
```
mvn versions:commit
```