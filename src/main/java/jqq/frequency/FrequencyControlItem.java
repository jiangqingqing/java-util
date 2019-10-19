package jqq.frequency;

import java.util.Date;

/**
 * 类/接口注释
 * 频度控制的实体
 * @since v1.1.0
 * @author 
 * @createDate 2012-2-22
 * 
 */
class FrequencyControlItem {

    /**
     * @param id 由调用方控制的id
     * @param maxTimes 最大次数
     * @param frequencyControlType 频度控制的类别
     * @param startTime 开始生效时间,null,则立即生效
     * @param expireTime 过期时间,null,则永无过期
     * @param timeIntervalType 时间间隔类别
     * @param timeInterval(seconds)时间间隔类别是绝对时长时有效
     */
    public FrequencyControlItem(String id, int maxTimes, FrequencyControlType frequencyControlType, Date startTime, Date expireTime,
            TimeIntervalType timeIntervalType, long timeInterval) {
        super();
        this.id = id;
        this.maxTimes = maxTimes;
        this.frequencyControlType = frequencyControlType;
        this.startTime = startTime;
        this.expireTime = expireTime;
        this.timeIntervalType = timeIntervalType;
        this.timeInterval = timeInterval;
        if (expireTime != null) {
            if (expireTime.getTime() - new Date().getTime() <= 0) {
                throw new IllegalArgumentException("expireTime can not be before now , expireTime is  " + expireTime);
            }
        }
    }

    /**
     * @param id 由调用方控制的id
     * @param maxTimes 最大次数
     * @param frequencyControlType 频度控制的类别
     * @param timeIntervalType 时间间隔类别
     * @param timeInterval(seconds)时间间隔类别是绝对时长时有效
     */
    public FrequencyControlItem(String id, int maxTimes, FrequencyControlType frequencyControlType, TimeIntervalType timeIntervalType,
            long timeInterval) {
        super();
        this.id = id;
        this.maxTimes = maxTimes;
        this.frequencyControlType = frequencyControlType;
        this.startTime = null;
        this.expireTime = null;
        this.timeIntervalType = timeIntervalType;
        this.timeInterval = timeInterval;
        if (expireTime != null) {
            if (expireTime.getTime() - new Date().getTime() <= 0) {
                throw new IllegalArgumentException("expireTime can not be before now , expireTime is  " + expireTime);
            }
        }
    }

    /**
     * 
     * @param id
     * @param maxTimes 最大次数
     * @param timeInterval 时间间隔
     */
    public FrequencyControlItem(String id, int maxTimes, long timeInterval) {
        super();
        this.id = id;
        this.maxTimes = maxTimes;
        this.frequencyControlType = FrequencyControlType.SIMPLE_CUMULATE_FREQUENCY;
        this.startTime = null;
        this.expireTime = null;
        this.timeIntervalType = TimeIntervalType.ABOSULTE_TIME_PERIOD;
        this.timeInterval = timeInterval;
    }

    public static enum TimeIntervalType {
        //绝对时长
        ABOSULTE_TIME_PERIOD,

        //自然小时
        NATRUAL_HOUR,

        //自然天
        NATRUAL_DAY,

        //自然月
        NATRUAL_MONTH;

        public static TimeIntervalType get(int i) {
            if (i > TimeIntervalType.values().length) {
                throw new IllegalArgumentException("");
            }
            return TimeIntervalType.values()[i];
        }
    }

    public static enum FrequencyControlType {

        //简单累计的频度控制类型
        SIMPLE_CUMULATE_FREQUENCY,

        //关联排重的频度控制类型
        RELATE_UNIQE_FREQUENCY;

        public static FrequencyControlType get(int i) {
            if (i > TimeIntervalType.values().length) {
                throw new IllegalArgumentException("");
            }
            return FrequencyControlType.values()[i];
        }
    }

    // 频度控制的id(由调用方去保证唯一)
    private String id;

    // 频度控制的最大次数
    private int maxTimes;

    // 类别
    private FrequencyControlType frequencyControlType;

    //生效时间
    private Date startTime;

    //过期时间
    private Date expireTime;

    //频度类别(固定时长,自然时长)
    private TimeIntervalType timeIntervalType;

    //时间间隔,频度类别为绝对时长时,该属性有效
    private long timeInterval;

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public TimeIntervalType getTimeIntervalType() {
        return timeIntervalType;
    }

    public void setTimeIntervalType(TimeIntervalType timeIntervalType) {
        this.timeIntervalType = timeIntervalType;
    }

    public FrequencyControlType getFrequencyControlType() {
        return frequencyControlType;
    }

    public void setFrequencyControlType(FrequencyControlType frequencyControlType) {
        this.frequencyControlType = frequencyControlType;
    }

}
