package facade;

import org.springframework.beans.factory.FactoryBean;

public class QRPCSpringProvider implements FactoryBean {

    private QRPCProvider qrpcProvider;

    private String version;

    private String interfaceName;

    private Object target;

    public void init() throws Exception {
        qrpcProvider = new QRPCProvider();
        qrpcProvider.setTarget(target);
        qrpcProvider.setInterfaceName(interfaceName);
        qrpcProvider.setVersion(version);
        qrpcProvider.init();
    }

    public QRPCProvider getQrpcProvider() {
        return qrpcProvider;
    }

    public void setQrpcProvider(QRPCProvider qrpcProvider) {
        this.qrpcProvider = qrpcProvider;
    }

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

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object getObject() throws Exception {
        return target;
    }

    @Override
    public Class<?> getObjectType() {
        return target.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
