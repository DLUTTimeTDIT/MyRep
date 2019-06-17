package facade;

import org.springframework.beans.factory.FactoryBean;

public class QRPCSpringConsumer implements FactoryBean {

    private String version;

    private String interfaceName;

    private QRPCConsumer qrpcConsumer;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public Object getObject() throws Exception {
        return qrpcConsumer.getTarget();
    }

    @Override
    public Class<?> getObjectType() {
        return qrpcConsumer.getTarget().getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void init() throws Exception{
        qrpcConsumer = new QRPCConsumer();
        qrpcConsumer.setInterfaceName(interfaceName);
        qrpcConsumer.setVersion(version);
        qrpcConsumer.init();
    }
}
