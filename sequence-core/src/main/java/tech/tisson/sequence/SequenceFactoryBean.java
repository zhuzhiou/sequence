package tech.tisson.sequence;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.shaded.com.google.common.io.ByteArrayDataOutput;
import org.apache.curator.shaded.com.google.common.io.ByteStreams;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.FactoryBean;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * 序列产生器工厂，关键代码是获取到 snowflake 算法中的 workerId
 *
 * @author zhuzhiou
 */
public class SequenceFactoryBean implements FactoryBean<Sequence> {

    private final static String SEQUENCE_ROOT_PATH = "/snowflake";

    private String endpoint;

    private String connectString;

    /**
     * 端点地址，如：192.168.1.190:8080
     *
     * @param endpoint 序列服务的端点地址
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * zookeeper 连接地址，如：localhost:2181
     *
     * @param connectString zookeeper连接地址
     */
    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 只实现了一个类，工厂也只能构造这个实现类了
     *
     * @return 序列产生器
     * @throws Exception zookeeper操作会引发该异常
     */
    @Override
    public Sequence getObject() throws Exception {
        try (CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(connectString)
                .retryPolicy(new RetryUntilElapsed(2000, 4))
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(6000)
                .build()) {
            curator.start();
            /*
             * 如果根节点不存在，创建 /snowflake 节点
             */
            Stat stat = curator.checkExists().forPath(SEQUENCE_ROOT_PATH);
            if (stat == null) {
                curator.create().withMode(CreateMode.PERSISTENT).forPath(SEQUENCE_ROOT_PATH);
            }
            String workerIdPath = curator.getChildren().forPath(SEQUENCE_ROOT_PATH)
                    .stream()
                    .filter(key -> StringUtils.startsWith(key, endpoint + "-"))
                    .map(key -> SEQUENCE_ROOT_PATH + "/" + key)
                    .findFirst()
                    .orElseGet(() -> {
                        try {
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeLong(System.currentTimeMillis());
                            return curator.create()
                                    .creatingParentsIfNeeded()
                                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                                    .forPath(SEQUENCE_ROOT_PATH + "/" + endpoint + "-", out.toByteArray());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            /*
             * 如果时间小于节点的时间戳，会重复序列的风险
             */
            if (ByteStreams.newDataInput(curator.getData().forPath(workerIdPath)).readLong() > System.currentTimeMillis()) {
                throw new RuntimeException("出现时间回拨错误，请检查服务器时间");
            }
            /*
             * 将最新时间戳写到节点
             */
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeLong(System.currentTimeMillis());
            curator.setData().forPath(workerIdPath, out.toByteArray());
            return new SnowFlake(parseInt(substringAfter(workerIdPath, "-")));
        }
    }

    @Override
    public Class<?> getObjectType() {
        return Sequence.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
