package northeastern.xiaosongzhai.medical.repository;

import northeastern.xiaosongzhai.medical.model.Plan;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/4/11 7:08
 * @Description: repository layer for plan
 */
public interface PlanRepository extends ElasticsearchRepository<Plan, String> {

}
