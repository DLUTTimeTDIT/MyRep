package facade;

import model.MetaData;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务消费方
 */
public class QRPCConsumer {

    // 默认服务版本 0.0.1
    private final String DEFAULT_VERSION = "0.0.1";

    // 是否初始化了
    private final AtomicBoolean inited = new AtomicBoolean(false);

    // 待消费服务对应的元数据
    private MetaData metaData;

}
