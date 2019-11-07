package tech.tisson.sequence;

import java.util.Random;

/**
 * 使用 SnowFlake 算法实现序列产生器
 *
 * @author zhuzhiou
 */
public class SnowFlake implements Sequence {

    private Random random = new Random();

    /**
     * 起始的时间戳
     */
    private final long twepoch = 1546300800000L;
    /**
     * 机器标识位数
     */
    private final long workerIdBits = 10L;
    /**
     * 机器标识最大值
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /**
     * 毫秒内自增位
     */
    private final long sequenceBits = 12L;
    /**
     * 序列最大值
     */
    private final long maxSequence = -1L ^ (-1L << sequenceBits);

    /**
     * 每一部分向左的位移
     */
    private final long workerIdShift = sequenceBits;
    private final long timestampLeft = sequenceBits + workerIdBits;

    private final int workerId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    SnowFlake(int workerId) {
        /*
         * 最大数计算公式：~(-1L << 10)
         */
        if (workerId >= maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("workerId 必须要大于等于0并且小于" + maxWorkerId);
        }
        this.workerId = workerId;
    }

    /**
     * 产生下一个ID
     */
    public synchronized long next() {
        long currStmp = getNewStmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("系统时间出现回拨，请调整系统时间");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & maxSequence;
            //同一毫秒的序列数已经达到最大，则等待下一秒
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号循环使用
            sequence = random.nextInt(10);
        }

        lastStmp = currStmp;

        return (currStmp - twepoch) << timestampLeft //时间戳部分
                | workerId << workerIdShift             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewStmp();
        while (mill <= lastStmp) {
            mill = getNewStmp();
        }
        return mill;
    }

    private long getNewStmp() {
        return System.currentTimeMillis();
    }
}
