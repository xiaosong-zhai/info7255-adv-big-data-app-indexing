package northeastern.xiaosongzhai.medical.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Valid
    @NotNull
    private String _org;
    @NotNull
    private String objectId;
    @NotNull
    private String objectType;
    @NotNull
    private String name;
}
