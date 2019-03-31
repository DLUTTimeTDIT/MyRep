package common;

import constants.ProxyTypeEnum;
import constants.SerializeTypeEnum;

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
    private Long executeTimeout;

    /**
     * 服务端客户端心跳时间
     */
    private Long IdleTimeout;

    /**
     * 序列化方式
     */
    private SerializeTypeEnum serializeType;

    /**
     * 代理方式
     */
    private ProxyTypeEnum proxyType;

    /**
     * 服务唯一标识名字
     */
    private volatile String uniqueName;

    /**
     * 代理对象
     */
    private volatile Object target;
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

    public Long getExecuteTimeout() {
        return executeTimeout;
    }

    public void setExecuteTimeout(Long executeTimeout) {
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

    public ProxyTypeEnum getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyTypeEnum proxyType) {
        this.proxyType = proxyType;
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
}
