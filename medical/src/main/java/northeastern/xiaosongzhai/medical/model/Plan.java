package northeastern.xiaosongzhai.medical.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 21:17
 * @Description: This is a class for Plan model
 */
@Document(indexName = "index_plan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan implements Serializable {
    @Serial
    private static final long serialVersionUID = -4299494647370945132L;

    @Field(type = FieldType.Nested)
    @Valid
    private PlanCostShares planCostShares;

    @Field(type = FieldType.Nested)
    @Valid
    private List<LinkedPlanService> linkedPlanServices;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String _org;

    @Id
    @NotNull
    private String objectId;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String objectType;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String planType;

    @Field(type = FieldType.Date, format = {}, pattern = "dd-MM-yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @NotNull
    private String creationDate;
}
