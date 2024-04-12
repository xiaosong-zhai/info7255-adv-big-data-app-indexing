package northeastern.xiaosongzhai.medical.message;

import northeastern.xiaosongzhai.medical.config.RabbitMQConfig;
import northeastern.xiaosongzhai.medical.constant.CommonConstants;
import northeastern.xiaosongzhai.medical.repository.PlanRepository;
import northeastern.xiaosongzhai.medical.service.impl.PlanServiceImpl;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/4/11 14:48
 * @Description: message consumer for plan
 */
@Service
public class PlanMessageConsumer {
    @Autowired
    private PlanServiceImpl planService;

    @Autowired
    private PlanRepository planRepository;


    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receivePlanMessage(PlanMessage planMessage) {
        try {
            switch (planMessage.getType()) {
                case "create":
                    planService.storePlan(planMessage.getPlan(), planMessage.getAdditionalProperties().get(CommonConstants.ETAG_KEY).toString());
                    planRepository.save(planMessage.getPlan());
                    break;
                case "update":
                    planRepository.save(planMessage.getPlan());
                    break;
                case "delete":
                    planService.deletePlanById(planMessage.getObjectId());
                    planRepository.deleteById(planMessage.getObjectId());
                    break;
            }
        } catch (DuplicateKeyException e) {
            // Log and handle duplicate key exception
            e.printStackTrace();
        } catch (Exception e) {
            // Log and handle other exceptions
            e.printStackTrace();
        }
    }
}
