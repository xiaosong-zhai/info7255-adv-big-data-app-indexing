package northeastern.xiaosongzhai.medical.controller;

import java.io.Serial;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import northeastern.xiaosongzhai.medical.constant.CommonConstants;
import northeastern.xiaosongzhai.medical.message.PlanMessage;
import northeastern.xiaosongzhai.medical.message.PlanMessageProducer;
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

    @Autowired
    private PlanMessageProducer planMessageProducer;

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


        PlanMessage planMessage = PlanMessage.builder()
                .type(CommonConstants.METHOD_CREATE)
                .plan(plan)
                .objectId(plan.getObjectId())
                .additionalProperties(new HashMap<String, Object>() {
                    @Serial
                    private static final long serialVersionUID = 2569796126586520489L;

                    {
                    put(CommonConstants.ETAG_KEY, eTagValue);
                }})
                .build();

        // publish message to rabbitmq
        try {
            planMessageProducer.sendPlanMessage(planMessage);
        } catch (Exception e) {
            e.printStackTrace();  // 或者使用日志框架记录异常信息
        }

        // planService.storePlan(plan, eTagValue);
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
    public ResponseEntity<Object> deleteModelById(@RequestHeader HttpHeaders headers, @PathVariable String objectId) {

        PlanMessage planMessage = PlanMessage.builder()
                .type(CommonConstants.METHOD_DELETE)
                .objectId(objectId)
                .build();
//        // get eTag from client request
//        String clientETag = headers.getFirst(CommonConstants.IF_MATCH);
//
//        // if header is not present, return 428 Precondition Required
//        if (clientETag == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("If-Match header is not present");
//        }

        // publish message to rabbitmq
        planMessageProducer.sendPlanMessage(planMessage);

        // planService.deletePlanById(objectId);
        return ResponseEntity.noContent().build();
    }


    /**
     * patch model by id
     * @param objectId objectId
     */
    @PatchMapping("/{objectId}")
    public ResponseEntity<Object> patchModelById(@RequestHeader HttpHeaders headers,@PathVariable String objectId, @Validated @RequestBody Plan plan) {
        // get eTag from client request
        String clientETag = headers.getFirst(CommonConstants.IF_MATCH);

        // if header is not present, return 428 Precondition Required
        if (clientETag == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body("If-Match header is not present");
        }

        // check if client eTag matches server eTag
        String eTagKey = CommonConstants.ETAG_KEY + ":" + objectId;
        String serverETag = (String) planService.getETagValue(eTagKey);
        if (!Objects.equals(clientETag, serverETag)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("If-Match header does not match server eTag");
        }

            //Attempt to patch the plan by id
            Plan updatedPlan = planService.patchPlanById(objectId, plan);
            if (updatedPlan == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("objectId: " + objectId + " not found");
            }

        // publish message to rabbitmq
        planMessageProducer.sendPlanMessage(PlanMessage.builder()
                .type(CommonConstants.METHOD_UPDATE)
                .plan(updatedPlan)
                .objectId(objectId)
                .build());

        String afterPatchETag = planService.getETagValue(eTagKey).toString();

        return ResponseEntity.ok().header(HttpHeaders.ETAG, afterPatchETag).body(updatedPlan);
    }
}
