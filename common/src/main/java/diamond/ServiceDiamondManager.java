package diamond;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import constants.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * 确保一个JVM里只有一个service为dataId的DiamondManager
 */
public class ServiceDiamondManager {

    private static final String SERVICE_DATA_ID = "service";

    private static volatile List<String> serviceList = new CopyOnWriteArrayList<>();

    private static DiamondManager diamondManager = new DefaultDiamondManager(Constants.DIAMOND_GROUP_NAME, SERVICE_DATA_ID, new ManagerListener() {
        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            if(configInfo == null || "".equals(configInfo)){
                serviceList = new CopyOnWriteArrayList<>();
                return;
            }
            String[] servicesStr = configInfo.split(",");
            ServiceDiamondManager.serviceList = new CopyOnWriteArrayList<>(Arrays.asList(servicesStr));
        }
    });

    public static List<String> getServiceList(){
        return serviceList;
    }
}
