import facade.QRPCConsumer;
import testservice.TestService;

import java.util.Timer;
import java.util.TimerTask;

public class MainTestConsumer {

    /**
     * 预热时间
     */
    private static final long PRE_RUNTIME = 30000L;

    /**
     * 每个线程预热时间
     */
    private static final long PRE_RUNTIME_EACH_THREAD = 60000L;

    /**
     * 测试时间范围
     */
    private static final long TIME_SPAN = 10000L;

    /**
     * 测试线程个数
     */
    private static final int THREAD_COUNT = 30;

    private static volatile boolean stop = false;

    public static void main(String[] args) {
        QRPCConsumer qrpcConsumer = new QRPCConsumer();
        qrpcConsumer.setInterfaceName(TestService.class.getName());
        TestService testService = null;
        try {
            qrpcConsumer.init();
            Object obj = qrpcConsumer.getTarget();
            testService = (TestService)obj;
            Timer timer = new Timer();
            // schedules the task to be run in an interval
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    stop = true;
                    this.cancel();
                }
            }, PRE_RUNTIME, PRE_RUNTIME);
            while(!stop) {
                testService.helloWorld("test");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 预热10s 结束 开始测试
        long startTime = System.currentTimeMillis();
        int i = 0;
        while(i < THREAD_COUNT){
            new Thread(new TestRunnable(startTime, testService)).start();
            i++;
        }
    }

    static class TestRunnable implements Runnable {

        private TestService testService;

        private long startTime;

        public TestRunnable(long startTime, TestService testService) {
            this.startTime = startTime;
            this.testService = testService;
        }

        @Override
        public void run() {

            long preRunEndTime = startTime + PRE_RUNTIME_EACH_THREAD;
            while(System.currentTimeMillis() < preRunEndTime){
                testService.helloWorld("test");
            }
            int count = 0;
            long endTime = System.currentTimeMillis() + TIME_SPAN;
            while(System.currentTimeMillis() < endTime){
                testService.helloWorld("test");
                count++;
            }
            System.out.println(count);
        }
    }
}
