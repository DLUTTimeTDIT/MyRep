package model;

import constants.SerializeTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务相关的元数据
 */
public class MetaData {

    /**
     * 服务接口名
     */
    private String interfaceName;

    /**
     * 服务版本号
     */
    private String version;

    /**
     * 服务接口对应类
     */
    private Class<?> interfaceClazz;

    /**
     * 调用超时时间 单位毫秒
     */
    private Integer executeTimeout = 3000;

    /**
     * 服务端客户端心跳时间
     */
    private Long IdleTimeout;

    /**
     * 序列化方式
     */
    private SerializeTypeEnum serializeType;

    /**
     * 服务唯一标识名字
     */
    private volatile String uniqueName;

    /**
     * 核心池大小
     */
    private int corePoolSize = 0;

    /**
     * 最大线程个数大小
     */
    private int maxPoolSize = 0;

    /**
     * 代理对象
     */
    private volatile Object target;

    private Integer routeType;

    /**
     * 一致性hash的key
     */
    private String consistent;

    /**
     * 是否是广播 如果是 调用的时候会对所有可调用列表依次调用 成功一次即停止
     */
    private boolean broadcast = false;

    /**
     * 业务线程线程池中还可以使用的线程个数
     */
    private AtomicInteger availablePoolSize;

    private int connectionNum = 1;

    private final transient AtomicLong connectionIndex = new AtomicLong(0);

    public int getConnectionIndex(){
        if(connectionNum == 1){
            return 0;
        }
        return (int)connectionIndex.getAndIncrement() % connectionNum;
    }
    // todo 先暂时列这么多


    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getExecuteTimeout() {
        return executeTimeout;
    }

    public void setExecuteTimeout(Integer executeTimeout) {
        this.executeTimeout = executeTimeout;
    }

    public Long getIdleTimeout() {
        return IdleTimeout;
    }

    public void setIdleTimeout(Long idleTimeout) {
        IdleTimeout = idleTimeout;
    }

    public SerializeTypeEnum getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeTypeEnum serializeType) {
        this.serializeType = serializeType;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class<?> getInterfaceClazz() {
        return interfaceClazz;
    }

    public void setInterfaceClazz(Class<?> interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        setAvailablePoolSize(maxPoolSize);
        this.maxPoolSize = maxPoolSize;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public String getConsistent() {
        return consistent;
    }

    public void setConsistent(String consistent) {
        this.consistent = consistent;
    }

    public AtomicInteger getAvailablePoolSize() {
        return availablePoolSize;
    }

    private void setAvailablePoolSize(int poolSize) {
        if(availablePoolSize != null){
            int availablePoolSize = this.maxPoolSize - this.availablePoolSize.get();
            this.availablePoolSize.set(poolSize - availablePoolSize);
        } else{
            this.availablePoolSize = new AtomicInteger(poolSize);
        }
    }

    public int getConnectionNum() {
        return connectionNum;
    }

    public void setConnectionNum(int connectionNum) {
        this.connectionNum = connectionNum;
    }
}
