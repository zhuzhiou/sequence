# Docker Swarm 部署

## 打包 spring boot 工程

```
mvn clean package
```

## 构建 Docker Image

```
docker build -t sequence .
```

## 创建 overlay 网络

```
docker network create -d overlay sequence
```

## 启动 zookeeper 服务

```
docker service create \
--network sequence \
--name zookeeper \
--publish published=2181,target=2181 \
zookeeper:latest
```

## 启动 sequence 服务

```
docker service create \
--network sequence \
--name sequence \
--publish published=8080,target=8080 \
--env zookeeper.connectString=zookeeper:2181 \
sequence-server:latest
```