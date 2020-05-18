package sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomHandleObject<T> implements Handler<JsonNode>{

    private Handler<T> handler;
    private Class<? extends T> messageType;
    private ObjectMapper objectMapper;

    @Override
    public void handle(JsonNode message) throws Exception {
        handler.handle(objectMapper.treeToValue(message, messageType));
    }
}
