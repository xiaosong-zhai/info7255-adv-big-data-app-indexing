package northeastern.xiaosongzhai.medical.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 21:34
 * @Description: This is a class for LinkedPlanService model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedPlanService implements Serializable {
    @Serial
    private static final long serialVersionUID = -8421642217073716325L;

    @NotNull
    private LinkedService linkedService;
    @NotNull
    private PlanServiceCostShares planserviceCostShares;
    @NotNull
    private String _org;
    @NotNull
    private String objectId;
    @NotNull
    private String objectType;
}
