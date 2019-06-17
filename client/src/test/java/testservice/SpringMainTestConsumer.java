package testservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMainTestConsumer {


    @Autowired
    private TestService testService;


    public static void main(String[] args) {
        String[] locations = {"applicationContext.xml"};
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext(locations);
        TestService testService = (TestService) ctx.getBean("testService");
        System.out.println(testService.helloWorld("shuqi"));
    }

}
