package northeastern.xiaosongzhai.medical.model;

import jakarta.validation.Valid;
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
 * @Date: 2024/2/6 21:37
 * @Description: This is a class for LinkedService model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkedService implements Serializable {
    @Serial
    private static final long serialVersionUID = 4897374593153573406L;

    @Field(type = FieldType.Keyword)
    @Valid
    @NotNull
    private String _org;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String objectId;

    @Field(type = FieldType.Keyword)
    @NotNull
    private String objectType;

    @Field(type = FieldType.Text, analyzer = "english")
    @NotNull
    private String name;
}
