package northeastern.xiaosongzhai.medical.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import northeastern.xiaosongzhai.medical.config.StrictIntegerDeserializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 21:33
 * @Description: This is a class for PlanCostShares model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanCostShares implements Serializable {
    @Serial
    private static final long serialVersionUID = 6868787983378508075L;

    @NotNull
    @JsonDeserialize(using = StrictIntegerDeserializer.class)
    private Integer deductible;
    @NotNull
    private String _org;
    @NotNull
    @JsonDeserialize(using = StrictIntegerDeserializer.class)
    private Integer copay;
    @NotNull
    private String objectId;
    @NotNull
    private String objectType;
}
