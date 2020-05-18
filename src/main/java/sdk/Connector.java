package sdk;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;

public class Connector {
    private ClientEndPoint clientEndPoint;
    private ApplicationContext applicationContext;
    private ObjectMapper objectMapper;
    private ConsumerManager consumerManager;

    public Connector(String uri, ApplicationContext ac) {
        applicationContext = ac;
        objectMapper = applicationContext.getBean("objectMapper", ObjectMapper.class);
        consumerManager = (ConsumerManager) applicationContext.getBean("consumerManager");
        clientEndPoint = new ClientEndPoint((ConsumerManager) applicationContext.getBean("consumerManager"), objectMapper);
        clientEndPoint.connect(uri);
    }

    public Producer producer(String queue) {
        return new Producer(this, queue);
    }

    public void send(Object message, String url) {
        try {
            clientEndPoint.syncSendMessage(objectMapper.writeValueAsString(message), url);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized Consumer consumer(String queue) {
        return new Consumer(this, objectMapper, queue);
    }

    public synchronized boolean subscribe(String queue, Consumer consumer) {
        return consumerManager.putCurrentSession(queue, consumer);
    }

    public void close() {
        clientEndPoint.disconnect();
    }

    public void subscribe(String subscribe) {
        clientEndPoint.subscribe(subscribe);
    }
}
