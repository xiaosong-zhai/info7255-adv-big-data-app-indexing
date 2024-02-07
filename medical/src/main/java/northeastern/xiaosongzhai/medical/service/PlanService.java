package northeastern.xiaosongzhai.medical.service;

import northeastern.xiaosongzhai.medical.model.Plan;

import java.util.List;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 23:16
 * @Description: service layer for plan
 */
public interface PlanService {

    void storePlan(Plan plan, String eTagValue);

    List<Plan> getAllPlans();

    Plan getPlanById(String objectId);

    Object getETagValue(String eTagKey);

    void deletePlanById(String objectId);
}
