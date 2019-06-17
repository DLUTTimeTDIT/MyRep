import facade.QRPCProvider;
import testservice.TestService;
import testservice.impl.TestServiceImpl;

public class MainTestProvider {

    public static void main(String[] args) {
        QRPCProvider qrpcProvider = new QRPCProvider();
        qrpcProvider.setInterfaceName(TestService.class.getName());
        qrpcProvider.setTarget(new TestServiceImpl());
        try {
            qrpcProvider.init();
            Thread.sleep(999999999);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
