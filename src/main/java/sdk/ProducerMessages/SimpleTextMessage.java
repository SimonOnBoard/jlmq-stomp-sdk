package sdk.ProducerMessages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sdk.Message;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleTextMessage implements Message {
    private String text;
}
