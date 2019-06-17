package facade;

import constants.SerializeTypeEnum;
import container.ServiceSingletonContainer;
import model.MetaData;
import process.ProcessService;
import process.impl.ProcessServiceImpl;
import utils.ClassUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务消费方
 */
public class QRPCConsumer {

    // 默认服务版本 0.0.1
    private final String DEFAULT_VERSION = "1.0.0";

    // 是否初始化了
    private final AtomicBoolean inited = new AtomicBoolean(false);

    // 待消费服务对应的元数据
    private MetaData metaData = new MetaData();

    public QRPCConsumer() {
        // 设置初始值
        metaData.setVersion(DEFAULT_VERSION);
        metaData.setExecuteTimeout(3000);
        metaData.setSerializeType(SerializeTypeEnum.KRYO.getType());
    }

    public void setInterfaceName(String interfaceName){
        metaData.setInterfaceName(interfaceName);
        try {
            metaData.setInterfaceClazz(ClassUtils.name2class(interfaceName));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("请检查接口名是否正确:" + interfaceName);
        }
        metaData.setUniqueName(interfaceName + this.metaData.getVersion());
    }

    public void setVersion(String version){
        metaData.setVersion(version);
    }

    public void init() throws Exception {
        // 避免被初始化多次
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        ProcessService processService = ServiceSingletonContainer.getInstance(ProcessServiceImpl.class);
        try {
            metaData.setTarget(processService.consume(metaData));

            System.out.println("接口消费成功，interfaceName:" + metaData.getInterfaceName());
        } catch (Exception e) {
            throw e;
        }
    }

    public Object getTarget(){
        return metaData.getTarget();
    }
}
