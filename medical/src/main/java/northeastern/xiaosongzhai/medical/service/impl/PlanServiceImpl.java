package northeastern.xiaosongzhai.medical.service.impl;

import northeastern.xiaosongzhai.medical.constant.CommonConstants;
import northeastern.xiaosongzhai.medical.model.Plan;
import northeastern.xiaosongzhai.medical.service.PlanService;
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
                redisTemplate.opsForValue().set(planKey, plan);
                redisTemplate.opsForValue().set(eTagKey, eTagValue);
            }
        }
        redisTemplate.opsForValue().set(planKey, plan);
        redisTemplate.opsForValue().set(eTagKey, eTagValue);
    }

    /**
     * get all models from redis
     * @return list of use case models
     */
    @Override
    public List<Plan> getAllPlans() {
        // get all plans from redis
        List<Plan> models = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(CommonConstants.PLAN_PREFIX + "*").build();

        // scan all keys with prefix "plan:"
        redisTemplate.execute((RedisConnection connection) -> {
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                models.add((Plan) redisTemplate.opsForValue().get(key));
            }
            return null;
        });

        return models;
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

        // check if plan is existed
        if (Boolean.TRUE.equals(redisTemplate.hasKey(planKey))) {
            redisTemplate.delete(planKey);
            redisTemplate.delete(eTagKey);
        } else {
            throw new IllegalArgumentException("objectId: " + objectId + " not found");
        }

    }

}
