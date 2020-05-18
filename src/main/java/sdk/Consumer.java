package sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import sdk.ConsumerMessages.ConsumerMessage;

@Data
public class Consumer {
    private Connector connector;
    private Thread thread;
    private ObjectMapper objectMapper;
    private Handler<JsonNode> customHandler;

    public Consumer(Connector connector, ObjectMapper objectMapper, String queue) {
        this.connector = connector;
        this.objectMapper = objectMapper;
        thread = new SimpleForeverThread();
        thread.start();
        customHandler = new CustomHandleObject<>();
        subscribe(queue);
    }

    public void subscribe(String queue) {
        boolean result = connector.subscribe("/queue/" + queue, this);
        if (!result) {
            this.close();
            return;
        }
        connector.subscribe("/queue/" + queue);
    }

    public <T> void setHandler(Handler<T> type, Class<T> messageType) {
        customHandler = new CustomHandleObject<>(type, messageType, objectMapper);
    }

    public void handle(JsonNode node) throws Exception {
        customHandler.handle(node.get("payload"));
    }

    public void close() {
        thread.interrupt();
    }

}
