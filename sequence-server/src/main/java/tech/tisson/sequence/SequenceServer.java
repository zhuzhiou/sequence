package tech.tisson.sequence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用spring boot起一个序列服务
 *
 * @author zhuzhiou
 */
@SpringBootApplication
public class SequenceServer {

    public static void main(String[] args) {
        SpringApplication.run(SequenceServer.class, args);
    }
}
