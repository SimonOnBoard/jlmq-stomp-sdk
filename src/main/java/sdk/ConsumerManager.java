package sdk;


import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.*;

public class ConsumerManager {
    private final Map<String, Consumer> consumerMap = new HashMap<>();
    private final Map<String, StompSession.Subscription> subscriptionMap = new HashMap<>();

    public Optional<Consumer> getCurrentConsumer(String queue) {
        return Optional.ofNullable(consumerMap.get(queue));
    }

    public boolean putCurrentSession(String queue, Consumer consumer) {
        if (consumerMap.get(queue) != null) return false;
        consumerMap.put(queue, consumer);
        return true;
    }

    public void remove(String queue) {
        consumerMap.remove(queue);
    }

    public void closeAll() {
        Map<String, List<String>> headers = new HashMap<>();
        subscriptionMap.entrySet().stream().forEach(item -> {
            ArrayList<String> strings = new ArrayList<>();
            strings.add(item.getKey());
            headers.put("destination", strings);
            item.getValue().unsubscribe(StompHeaders.readOnlyStompHeaders(headers));
        });
        consumerMap.entrySet().stream().forEach(item -> item.getValue().close());
    }

    public void addSubscription(String url, StompSession.Subscription subscription) {
        subscriptionMap.put(url, subscription);
    }

    public StompSession.Subscription getSubscription(String url) {
        return subscriptionMap.get(url);
    }

    public void removeSubscription(String url) {
        subscriptionMap.remove(url);
    }
}
