package northeastern.xiaosongzhai.medical.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import northeastern.xiaosongzhai.medical.model.Plan;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/4/11 14:41
 * @Description: message for plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 6716052869372710435L;

    private String type; // "create", "update", "delete"

    private Plan plan;

    private String objectId; // For delete operation

    private Map<String, Object> additionalProperties;

//    public static class PlanMessageBuilder {
//        public PlanMessageBuilder additionalProperty(String key, Object value) {
//            if (this.additionalProperties == null) this.additionalProperties = new HashMap<>();
//            this.additionalProperties.put(key, value);
//            return this;
//        }
//    }
}
