import sdk.*;
import sdk.ProducerMessages.SimpleTextMessage;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Connector connector = JLMQ.connector("ws://localhost:8080/jlmq");
        Producer producer = connector.producer("toDo");
        Producer producer1 = connector.producer("toPrint");
        producer.sendTask(new SimpleTextMessage("Hi there"));
        Handler<SimpleTextMessage> messageHandler = message -> {
            System.out.println(message.getText());
        };
        Handler<SimpleTextMessage> messageHandler1 = message -> {
            System.out.println(message.getText());
        };
        Consumer consumer = connector.consumer("toDo");
        consumer.setHandler(messageHandler, SimpleTextMessage.class);
        Consumer consumer1 = connector.consumer("toPrint");
        consumer1.setHandler(messageHandler1, SimpleTextMessage.class);
        int i = 0;
        while (i < 100000) {
            producer.sendTask(new SimpleTextMessage("task_for_1"));
            producer1.sendTask(new SimpleTextMessage("task_for_2"));
            i++;
        }
        Thread.sleep(300000);
        connector.close();
    }
}
