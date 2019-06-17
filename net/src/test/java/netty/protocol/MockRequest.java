package netty.protocol;

import netty.request.RPCRequest;
import netty.utils.CodecTypeUtils;
import testservice.impl.TestServiceImpl;

public class MockRequest {

    private static String[] argsType = new String[]{String.class.getName()};

    private static String[] params = new String[1];

    private static byte[][] requestObjects = new byte[1][];

    private static int size = 0;

    static {
        params[0] = "params";
        requestObjects[0] = params[0].getBytes();
        size = argsType[0].length();
    }

    private static RPCRequest INSTANCE = new RPCRequest(3000, TestServiceImpl.class.getName(), "helloWorld",
            argsType, requestObjects, null, CodecTypeUtils.HESSIAN2_CODEC,  size );

    public static RPCRequest getInstance() {
        return INSTANCE;
    }
}
