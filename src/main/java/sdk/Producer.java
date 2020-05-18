package sdk;

import sdk.ProducerMessages.ProducerUniformMessage;

public class Producer {
    private Connector connector;
    private String queue;

    public Producer(Connector connector, String queue) {
        this.connector = connector;
        this.queue = queue;
        this.send(ProducerUniformMessage.builder().option("startProducer").build(), "/producer/startProducer");
    }

    public void sendTask(Object payload) {
        this.send(ProducerUniformMessage.builder().queue(queue).option("task").payload(payload).build(), "/producer/task");
    }

    private void send(ProducerUniformMessage message, String url) {
        connector.send(message, url);
    }
}
