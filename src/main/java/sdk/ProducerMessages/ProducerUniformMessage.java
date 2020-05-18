package sdk.ProducerMessages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProducerUniformMessage {
    private String queue;
    private String option;
    private Object payload;
}
