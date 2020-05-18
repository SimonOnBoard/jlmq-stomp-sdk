package sdk.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.*;
import sdk.ClientEndPoint;

import java.lang.reflect.Type;
import java.util.List;

public class SessionHandler extends StompSessionHandlerAdapter {
    private ObjectMapper objectMapper;
    private ClientEndPoint clientEndPoint;

    public SessionHandler(ObjectMapper objectMapper, ClientEndPoint clientEndPoint) {
        this.objectMapper = objectMapper;
        this.clientEndPoint = clientEndPoint;
    }

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        System.out.println("Connection established");
    }

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        System.err.println("HANDLING EXCEPTION FRAME" + "\n");
        if (throwable.getClass().equals(IllegalStateException.class)) {
            stompSession.disconnect();
        }
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {
        System.err.println("HANDLING TRANSPORT ERROR" + "\n");
        stompSession.disconnect();
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.err.println("HANDLING SESSION FRAME" + "\n");
        List<String> errors = headers.get("message");
        if (errors.size() != 0) {
            String result = errors.get(0);
            result = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
            try {
                JsonNode jsonNode = objectMapper.readTree(result);
                System.out.println(result);
                //String exception = jsonNode.get("exception").asText();
                String queue = jsonNode.get("queue").asText();
                int type = Integer.parseInt(jsonNode.get("type").asText());
                if (type == 1) {
                    clientEndPoint.unsubscribe(queue);
                }
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }

        }
        super.handleFrame(headers, payload);
    }
}
