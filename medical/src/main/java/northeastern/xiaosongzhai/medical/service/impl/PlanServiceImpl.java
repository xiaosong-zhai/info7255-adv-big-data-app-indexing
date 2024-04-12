package northeastern.xiaosongzhai.medical.service.impl;

import northeastern.xiaosongzhai.medical.constant.CommonConstants;
import northeastern.xiaosongzhai.medical.exception.CustomException;
import northeastern.xiaosongzhai.medical.model.LinkedPlanService;
import northeastern.xiaosongzhai.medical.model.Plan;
import northeastern.xiaosongzhai.medical.repository.PlanRepository;
import northeastern.xiaosongzhai.medical.service.PlanService;
import northeastern.xiaosongzhai.medical.utils.ETagUtil;
import northeastern.xiaosongzhai.medical.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 23:18
 * @Description: service implementation for plan
 */
@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    /**
     * store use case model into redis
     * @param plan use case model
     */
    @Override
    public void storePlan(Plan plan, String eTagValue) {

        String planKey = plan.getObjectType() + ":" + plan.getObjectId();
        String eTagKey = CommonConstants.ETAG_KEY + ":" + plan.getObjectId();

        // Attempt to retrieve the redis plan and ETag from Redis
        Plan redisPlan = (Plan) redisTemplate.opsForValue().get(planKey);
        String redisEtag = (String) redisTemplate.opsForValue().get(eTagKey);

        // If the plan exists in Redis, check if the ETag matches
        if (redisPlan != null && redisEtag != null) {
            // If the ETag matches, throw a DuplicateKeyException
            if (redisEtag.equals(eTagValue)) {
                throw new DuplicateKeyException(CommonConstants.DUPLICATE_KET_EXCEPTION);
            } else {
                // If the ETag does not match, update the plan and ETag in Redis
                storeModelToRedis(plan, eTagValue, planKey, eTagKey);
            }
        }
        storeModelToRedis(plan, eTagValue, planKey, eTagKey);
    }

    private void storeModelToRedis(Plan plan, String eTagValue, String planKey, String eTagKey) {
        try {
            redisTemplate.opsForValue().set(eTagKey, eTagValue);
            redisTemplate.opsForValue().set(planKey, plan);
            redisTemplate.opsForValue().set(planKey + ":" + CommonConstants.PLAN_COST_SHARES, plan.getPlanCostShares());
            redisTemplate.opsForValue().set(planKey + ":" + CommonConstants.LINKED_PLAN_SERVICES, plan.getLinkedPlanServices());
            // store linkedPlanServices, linkedService and planServiceCostShares
            for (int i = 0; i < plan.getLinkedPlanServices().size(); i++) {
                addNewLPSToRedis(plan, i);
            }
        } catch (Exception e) {
            throw new CustomException(CommonConstants.REDIS_EXCEPTION, e.getMessage());
        }
    }

    /**
     * get all models from redis
     * @return list of use case models
     */
    @Override
    public List<Plan> getAllPlans() {
        List<Plan> plans = new ArrayList<>();
        // Scan all keys with pattern "plan:*"
        String pattern = "plan:*";
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).build();

        redisTemplate.execute((RedisConnection connection) -> {
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                // Check if the key is a plan key
                if (!key.substring(key.indexOf(":") + 1).contains(":")) {
                    Plan plan = (Plan) redisTemplate.opsForValue().get(key);
                    if (plan != null) {
                        plans.add(plan);
                    }
                }
            }
            return null;
        }, true); // Use connection in pipeline mode for better performance

        return plans;
    }

    /**
     * get plan by id
     * @param objectId plan id
     * @return plan
     */
    @Override
    public Plan getPlanById(String objectId) {
        String key = CommonConstants.PLAN_PREFIX + objectId;

        // check if plan is existed
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return (Plan) redisTemplate.opsForValue().get(key);
        } else {
            return null;
        }

    }

    /**
     * get eTagValue from redis
     * @param eTagKey eTag key
     * @return eTag
     */
    @Override
    public Object getETagValue(String eTagKey) {
        return redisTemplate.opsForValue().get(eTagKey);
    }

    /**
     * delete plan by id
     * @param objectId plan id
     */
    @Override
    public void deletePlanById(String objectId) {
        String planKey = CommonConstants.PLAN_PREFIX + objectId;
        String eTagKey = CommonConstants.ETAG_KEY + ":" + objectId;
        String planCostSharesKey = planKey + ":" + CommonConstants.PLAN_COST_SHARES;
        String linkedPlanServicesKey = planKey + ":" + CommonConstants.LINKED_PLAN_SERVICES;

        // check if plan is existed
        if (Boolean.TRUE.equals(redisTemplate.hasKey(planKey))) {
            redisTemplate.delete(eTagKey);

            // delete linkedPlanServices, linkedService and planServiceCostShares
            Plan plan = (Plan) redisTemplate.opsForValue().get(planKey);
            for (int i = 0; i < Objects.requireNonNull(plan).getLinkedPlanServices().size(); i++) {
                String linkedPlanServiceKey = plan.getLinkedPlanServices().get(i).getObjectType() + ":" + plan.getLinkedPlanServices().get(i).getObjectId();
                String linkedServiceKey = plan.getLinkedPlanServices().get(i).getLinkedService().getObjectType() + ":" + plan.getLinkedPlanServices().get(i).getLinkedService().getObjectId();
                String planServiceCostSharesKey = plan.getLinkedPlanServices().get(i).getPlanserviceCostShares().getObjectType() + ":" + plan.getLinkedPlanServices().get(i).getPlanserviceCostShares().getObjectId();
                redisTemplate.delete(linkedPlanServiceKey);
                redisTemplate.delete(linkedServiceKey);
                redisTemplate.delete(planServiceCostSharesKey);
            }

            redisTemplate.delete(planKey);
            redisTemplate.delete(planCostSharesKey);
            redisTemplate.delete(linkedPlanServicesKey);

        } else {
            throw new IllegalArgumentException("objectId: " + objectId + " not found");
        }
    }

    /**
     * patch plan by id
     * @param objectId plan id
     * @param incomingPlan incoming plan
     * @return plan
     */
    @Override
    public Plan patchPlanById(String objectId, Plan incomingPlan) {
        String planKey = CommonConstants.PLAN_PREFIX + objectId;
        String eTagKey = CommonConstants.ETAG_KEY + ":" + objectId;

        Plan redisPlan = (Plan) redisTemplate.opsForValue().get(planKey);
        if (redisPlan == null) {
            throw new IllegalArgumentException("objectId: " + objectId + " not found");
        }

        if (redisPlan.getLinkedPlanServices() != null && incomingPlan.getLinkedPlanServices() != null) {
            for(int i = 0; i < incomingPlan.getLinkedPlanServices().size(); i++) {
                String incomingLPSKey = incomingPlan.getLinkedPlanServices().get(i).getObjectType() + ":" + incomingPlan.getLinkedPlanServices().get(i).getObjectId();
                if (Boolean.FALSE.equals(redisTemplate.hasKey(incomingPlan.getLinkedPlanServices().get(i).getObjectType() + ":" + incomingPlan.getLinkedPlanServices().get(i).getObjectId()))) {
                    redisPlan.getLinkedPlanServices().add(incomingPlan.getLinkedPlanServices().get(i));
                    addNewLPSToRedis(incomingPlan, i);
                }

                // check if hashcode is the same
                String incomingLPSHashCode = incomingPlan.getLinkedPlanServices().get(i).hashCode() + "";
                String redisLPSHashCode = Objects.requireNonNull(redisTemplate.opsForValue().get(incomingLPSKey)).hashCode() + "";
                if (!incomingLPSHashCode.equals(redisLPSHashCode)) {
                    redisPlan.getLinkedPlanServices().add(incomingPlan.getLinkedPlanServices().get(i));
                    addNewLPSToRedis(incomingPlan, i);
                }
            }
        }
        String updatedPlanJson = JsonUtil.toJson(redisPlan);
        redisTemplate.opsForValue().set(planKey, redisPlan);

        String newEtagValue = ETagUtil.generateETag(updatedPlanJson);
        redisTemplate.opsForValue().set(eTagKey, newEtagValue);

        return redisPlan;
    }

    private void addNewLPSToRedis(Plan incomingPlan, int i) {
        redisTemplate.opsForValue().set(incomingPlan.getLinkedPlanServices().get(i).getObjectType() + ":" + incomingPlan.getLinkedPlanServices().get(i).getObjectId(), incomingPlan.getLinkedPlanServices().get(i));
        redisTemplate.opsForValue().set(incomingPlan.getLinkedPlanServices().get(i).getLinkedService().getObjectType() + ":" + incomingPlan.getLinkedPlanServices().get(i).getLinkedService().getObjectId(), incomingPlan.getLinkedPlanServices().get(i).getLinkedService());
        redisTemplate.opsForValue().set(incomingPlan.getLinkedPlanServices().get(i).getPlanserviceCostShares().getObjectType() + ":" + incomingPlan.getLinkedPlanServices().get(i).getPlanserviceCostShares().getObjectId(), incomingPlan.getLinkedPlanServices().get(i).getPlanserviceCostShares());
    }


}
