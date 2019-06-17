package route.impl;

import org.apache.commons.lang.StringUtils;
import route.RouteService;
import utils.ThreadLocalUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash路由实现 考虑到对hash结果缓存可能对性能提升不大这里不做cache
 */
public class ConsistentRouteServiceImpl implements RouteService {

    public static final String CONSISTENT_KEY = "ck";

    private static final byte VIRTUAL_NODE_COUNT_FOR_EACH = 5;

    private static final String VIRTUAL_NODES_POSTFIX = "$VN";

    @Override
    public String selectAddress(List<String> address) {
        if (address == null || address.size() == 0) {
            return null;
        }
        String key = ThreadLocalUtils.get(CONSISTENT_KEY);
        // 虚拟节点的hash值与虚拟节点的map
        SortedMap<Integer, String> virtualNodes = buildVirtualMap(address);
        //得到该key的hash值
        int hash = getHash(key);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, String> subMap = virtualNodes.tailMap(hash);
        String virtualNode;
        if(subMap.isEmpty()){
            //如果没有比该key的hash值大的，则从第一个node开始
            Integer i = virtualNodes.firstKey();
            //返回对应的服务器
            virtualNode = virtualNodes.get(i);
        }else{
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            //返回对应的服务器
            virtualNode = subMap.get(i);
        }
        //virtualNode虚拟节点名称要截取一下
        if(StringUtils.isNotBlank(virtualNode)){
            return virtualNode.substring(0, virtualNode.indexOf("$"));
        }
        return null;


    }

    //使用FNV1_32_HASH算法计算服务器的Hash值
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    private SortedMap<Integer, String> buildVirtualMap(List<String> realNodes){

        SortedMap<Integer, String> virtualNodesMap = new TreeMap<>();
        for (String str : realNodes) {
            for (int i = 0; i < VIRTUAL_NODE_COUNT_FOR_EACH; i++) {
                String virtualNodeName = str + VIRTUAL_NODES_POSTFIX + i;
                int hash = getHash(virtualNodeName);
                virtualNodesMap.put(hash, virtualNodeName);
            }
        }
        return virtualNodesMap;
    }
}
