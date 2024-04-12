package northeastern.xiaosongzhai.medical.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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

    @Field(type = FieldType.Nested)
    @NotNull
    private LinkedService linkedService;

    @Field(type = FieldType.Nested)
    @NotNull
    private PlanServiceCostShares planserviceCostShares;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String _org;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String objectId;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String objectType;
}
