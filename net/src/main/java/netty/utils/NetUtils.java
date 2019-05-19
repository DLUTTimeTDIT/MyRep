package netty.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetUtils {

    private static String IP = null;
    public static String getIP(){
        if(IP != null) {
            return IP;
        }
        try {
            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while(interfaceEnumeration.hasMoreElements()){
                NetworkInterface networkInterface = interfaceEnumeration.nextElement();
                Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();
                while(addressEnumeration.hasMoreElements()){
                    address = addressEnumeration.nextElement();
                    if(!address.isLoopbackAddress() && !address.getHostAddress().contains(":")){
                        IP = address.getHostAddress();
                        return IP;
                    }
                }
            }
            IP = null;
            System.out.println("can not get Ip");
            return null;
        } catch (SocketException e) {
            System.out.println("get server Ip failed");
            e.printStackTrace();
            IP = null;
            return null;
        }
    }
}
