package diamond;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import constants.Constants;

/**
 * 确保该jvm中dataId对应的DiamondManager只有一个
 */
public class ServiceAddressDiamondUtil {

    private static Map<String, DiamondManager> dataIdManagerMap = new ConcurrentHashMap<>();

    public static void listenDataId(String dataId, DiamondStubObject diamondStubObject){
        DiamondManager diamondManager = dataIdManagerMap.get(dataId);
        if(diamondManager == null){
            diamondManager = new DefaultDiamondManager(Constants.DIAMOND_GROUP_NAME, dataId, new ManagerListener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    diamondStubObject.setValue(configInfo);
                }
            });
            dataIdManagerMap.put(dataId, diamondManager);
        }
    }
}
