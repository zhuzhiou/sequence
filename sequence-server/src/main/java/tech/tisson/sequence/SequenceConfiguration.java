package tech.tisson.sequence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

/**
 * 使用工厂构建一个序列产器，关键是拿到服务的端点，多网卡多地址的机器环境要谨慎配置。
 *
 * @author zhuzhiou
 */
@Configuration
public class SequenceConfiguration {

    @Bean
    public SequenceFactoryBean sequenceFactoryBean(@Value("${server.port:8080}") int listenPort,
                                                      @Value("${zookeeper.connectString:localhost:2181}") String connectString) throws SocketException {
        // 获取本机的所有IP地址
        List<InetAddress> inetAddresses = InetAddressCollection.getAllNonLoopInet4Address();
        if (inetAddresses.isEmpty()) {
            throw new RuntimeException("没有找到本机的IP地址");
        }
        /*
         * 如果有多个地址，取第一个地址
         */
        InetAddress inetAddress = inetAddresses.get(0);

        SequenceFactoryBean factoryBean = new SequenceFactoryBean();
        factoryBean.setConnectString(connectString);
        factoryBean.setEndpoint(inetAddress.getHostAddress() + ":" + listenPort);
        return factoryBean;
    }
}
