package sdk.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import sdk.ClientEndPoint;
import sdk.Consumer;

import java.lang.reflect.Type;
import java.util.*;

@Data
public class ConsumerFrameHandler implements StompFrameHandler {
    private ClientEndPoint clientEndPoint;
    private StompSession.Subscription subscription;
    private ObjectMapper objectMapper;
    private Consumer consumer;

    public ConsumerFrameHandler(ClientEndPoint clientEndPoint, ObjectMapper objectMapper, Optional<Consumer> currentConsumer) {
        this.clientEndPoint = clientEndPoint;
        this.objectMapper = objectMapper;
        //internal client error if null should be handled
        this.consumer = currentConsumer.get();
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        Map<String, List<String>> acknowledgeHeaders = new HashMap<>();
        List<String> messageParameters = new ArrayList<>();
        List<String> status = new ArrayList<>();
        String s = new String((byte[]) o);
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(s);
            setMessageId(messageParameters, jsonNode);

            setStatus(status, "1");
            constructMessageHeaders(messageParameters, status, acknowledgeHeaders);
            clientEndPoint.acknowledged(StompHeaders.readOnlyStompHeaders(acknowledgeHeaders), true);


            this.consumer.handle(jsonNode);

            setStatus(status, "2");
            constructMessageHeaders(messageParameters, status, acknowledgeHeaders);
            clientEndPoint.acknowledged(StompHeaders.readOnlyStompHeaders(acknowledgeHeaders), true);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void setStatus(List<String> status, String s) {
        status.clear();
        status.add(s);
    }

    private void constructMessageHeaders(List<String> messageParameters, List<String> status, Map<String, List<String>> acknowledgeHeaders) {
        acknowledgeHeaders.put("messageId", messageParameters);
        acknowledgeHeaders.put("status", status);
    }

    private void setMessageId(List<String> messageParameters, JsonNode jsonNode) {
        messageParameters.add(jsonNode.get("objectId").asText());
    }
}
