package northeastern.xiaosongzhai.medical.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;
import java.util.Objects;

import northeastern.xiaosongzhai.medical.constant.CommonConstants;
import northeastern.xiaosongzhai.medical.model.Plan;
import northeastern.xiaosongzhai.medical.service.PlanService;
import northeastern.xiaosongzhai.medical.utils.ETagUtil;
import northeastern.xiaosongzhai.medical.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 22:45
 * @Description: controller layer for plan
 */
@RestController
@RequestMapping("/v1/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    /**
     * store use case model into redis
     * @param plan use case model
     * @return response
     */
    @PostMapping
    public ResponseEntity<Plan> storeModel(@Validated @RequestBody Plan plan) {
        // generate eTag for plan
        String jsonPlan = JsonUtil.toJson(plan);
        String eTagValue = ETagUtil.generateETag(jsonPlan);

        // store plan into redis
        planService.storePlan(plan, eTagValue);
        // Build the response entity with status 201 Created
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(plan.getObjectId())
                .toUri();

        return ResponseEntity.created(location).header(CommonConstants.ETAG_KEY, eTagValue).body(plan);

    }

    /**
     * get all models from redis
     * @return list of use case models
     */
    @GetMapping
    public ResponseEntity< ? > getAllModels() {
        List<Plan> plans = planService.getAllPlans();
        if (plans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No plan found");
        }
        return ResponseEntity.ok().body(plans);
    }

    /**
     * get model by id
     * @param objectId objectId
     * @return model
     */
    @GetMapping("/{objectId}")
    public ResponseEntity<Object> getModelById(@RequestHeader HttpHeaders headers, @PathVariable String objectId) {
        // get eTag from client request
        String clientETag = headers.getFirst(CommonConstants.IF_NONE_MATCH);

        // Attempt to fetch the plan by id
        Plan plan = planService.getPlanById(objectId);
        if (plan == null) {
            // Early return if plan not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("objectId: " + objectId + " not found");
        }

        // Construct eTag key and retrieve the eTag value from a service or cache
        String eTagKey = CommonConstants.ETAG_KEY + ":" + objectId;
        String serverETag = (String) planService.getETagValue(eTagKey);

        // Check if eTag matches
        if (Objects.equals(clientETag, serverETag)) {
            // If eTags match, return 304 Not Modified without plan data
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).header(HttpHeaders.ETAG, serverETag).build();
        } else {
            // If eTags do not match, return the plan with current eTag
            return ResponseEntity.ok().header(HttpHeaders.ETAG, serverETag).body(plan);
        }
    }

    /**
     * delete model by id
     * @param objectId objectId
     */
    @DeleteMapping("/{objectId}")
    public ResponseEntity<Object> deleteModelById(@PathVariable String objectId) {
        planService.deletePlanById(objectId);
        return ResponseEntity.noContent().build();
    }

}
