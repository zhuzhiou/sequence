FROM java:8-jre
MAINTAINER 朱志欧<zhuzhiou@qq.com>
WORKDIR /usr/local/java
COPY sequence-server/target/sequence-server.jar ./sequence-generator.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/usr/local/java/sequence-generator.jar"]
