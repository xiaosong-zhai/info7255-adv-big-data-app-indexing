package northeastern.xiaosongzhai.medical.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 21:17
 * @Description: This is a class for Plan model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan implements Serializable {
    @Serial
    private static final long serialVersionUID = -4299494647370945132L;

    @Valid
    private PlanCostShares planCostShares;
    @Valid
    private List<LinkedPlanService> linkedPlanServices;
    @NotNull
    private String _org;
    @NotNull
    private String objectId;
    @NotNull
    private String objectType;
    @NotNull
    private String planType;
    @NotNull
    private String creationDate;
}
