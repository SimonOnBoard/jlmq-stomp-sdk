package sdk.ConsumerMessages;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConsumerMessage {
    private String option;
    private String queue;
    private String message;
    public static ConsumerMessage subscribe(String queue){
        return ConsumerMessage.builder()
                .option("subscribe")
                .queue(queue)
                .build();
    }

    public static ConsumerMessage notifyMessage(String objectId) {
        return ConsumerMessage.builder()
                .option("acknowledged")
                .message(objectId)
                .build();
    }

    public static ConsumerMessage completeMessage(String objectId, String queue) {
        return ConsumerMessage.builder()
                .option("completed")
                .message(objectId)
                .queue(queue)
                .build();
    }

}
