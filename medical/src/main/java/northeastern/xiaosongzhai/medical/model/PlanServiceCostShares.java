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
 * @Date: 2024/2/6 21:40
 * @Description: This is a class for PlanServiceCostShares model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanServiceCostShares implements Serializable {
    @Serial
    private static final long serialVersionUID = 105734589452017870L;

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
