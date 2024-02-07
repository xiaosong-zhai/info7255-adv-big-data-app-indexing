package northeastern.xiaosongzhai.medical.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

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
    public ResponseEntity<Plan> storeModel(@RequestBody
                                         @Validated
                                         Plan plan) {
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
    public ResponseEntity<List<Plan>> getAllModels() {
        List<Plan> plans = planService.getAllPlans();
        return ResponseEntity.ok().body(plans);
    }

    /**
     * get model by id
     * @param objectId objectId
     * @return model
     */
    @GetMapping("/{objectId}")
    public ResponseEntity<Object> getModelById(@RequestHeader HttpHeaders headers,
                                             @PathVariable String objectId) {
        // get eTag from client request
        String clientETag = headers.getFirst(CommonConstants.IF_NONE_MATCH);

        // get plan by id
        Plan plan = planService.getPlanById(objectId);

        // get eTag from redis
        String eTagKey = CommonConstants.ETAG_KEY + ":" + objectId;
        String serverETag = (String) planService.getETagValue(eTagKey);

        if (clientETag == null) {
            // check if plan is existed
            if (plan == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("objectId: " + objectId + " not found");
            }
            return ResponseEntity.ok().header(CommonConstants.ETAG_KEY, serverETag).body(plan);
        } else {
            // check if eTag is matched
            if (clientETag.equals(serverETag)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).header(CommonConstants.ETAG_KEY, serverETag).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).header(CommonConstants.ETAG_KEY, serverETag).body(plan);
            }
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
