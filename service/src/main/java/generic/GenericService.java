package generic;

import exception.QRPCException;

public interface GenericService {

    Object $invoke(String methodName, String[] paramTypes, Object[] args) throws QRPCException;
}
