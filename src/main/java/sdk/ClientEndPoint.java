package sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import sdk.handlers.ConsumerFrameHandler;
import sdk.handlers.SessionHandler;

import java.util.Collections;
import java.util.List;

public class ClientEndPoint {
    private ConsumerManager consumerManager;
    private ObjectMapper objectMapper;
    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private StompSession stompSession;
    private WebSocketStompClient stompClient;

    public ClientEndPoint(ConsumerManager consumerManager, ObjectMapper objectMapper) {
        this.consumerManager = consumerManager;
        this.objectMapper = objectMapper;
    }


    public void connect(String serverUrl) {
        try {
            if (stompClient == null) {
                WebSocketTransport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
                SockJsClient sockJsClient;
                List<Transport> transports = Collections.singletonList(webSocketTransport);
                sockJsClient = new SockJsClient(transports);
                sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
                stompClient = new WebSocketStompClient(sockJsClient);
            }
            ListenableFuture<StompSession> s = stompClient.connect(serverUrl, headers, new SessionHandler(objectMapper, this));
            stompSession = s.get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void syncSendMessage(String msg, String url) {
        synchronized (stompSession) {
            if (!stompSession.isConnected()) this.connect("ws://localhost:8080/jlmq");
            stompSession.send(url, msg.getBytes());
        }
    }

    public void disconnect() {
        consumerManager.closeAll();
        stompSession.disconnect();
    }

    public void subscribe(String url) {
        StompSession.Subscription subscription = null;
        ConsumerFrameHandler consumerFrameHandler = new ConsumerFrameHandler(this, objectMapper, consumerManager.getCurrentConsumer(url));
        subscription = stompSession.subscribe(url, consumerFrameHandler);
        consumerManager.addSubscription(url, subscription);
        consumerFrameHandler.setSubscription(subscription);
        this.syncSendMessage("","/consumer" + url);
    }

    public void unsubscribe(String queue) {
        consumerManager.remove("/queue/" + queue);
        consumerManager.getSubscription("/queue/" + queue).unsubscribe();
    }

    public void acknowledged(StompHeaders headers, boolean result) {
        synchronized (stompSession) {
            if (!stompSession.isConnected()) this.connect("ws://localhost:8080/jlmq");
            stompSession.acknowledge(headers, result);
        }
    }
}