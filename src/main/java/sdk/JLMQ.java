package sdk;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JLMQ {
    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(sdk.utils.ApplicationContext.class);
    public static Connector connector(String uri){
        return new Connector(uri, applicationContext);
    }
}
