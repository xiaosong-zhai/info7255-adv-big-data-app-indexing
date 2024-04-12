package northeastern.xiaosongzhai.medical.message;

import northeastern.xiaosongzhai.medical.config.RabbitMQConfig;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/4/11 14:44
 * @Description: message producer for plan
 */
@Service
public class PlanMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PlanMessageProducer(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(messageConverter);
    }

    public void sendPlanMessage(PlanMessage planMessage) {
        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
            return message;
        };

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, planMessage, messagePostProcessor);
    }
}
