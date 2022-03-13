package eu.malycha.rabbitmq.demo.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class FirehoseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirehoseListener.class);

    private final TaskCounter taskCounter;

    public FirehoseListener(TaskCounter taskCounter) {
        this.taskCounter = taskCounter;
    }

    @RabbitListener(queues = FirehoseConfiguration.FIREHOSE_QUEUE, containerFactory = "auditContainerFactory")
    public void observe(String task, Message message) {
        MessageProperties properties = message.getMessageProperties();
        String routingKey = extractRoutingKey(properties);
        taskCounter.inc(routingKey);
        LOGGER.info("Task submitted to exchange: {} ({})", routingKey, task);
    }

    private static String extractRoutingKey(MessageProperties properties) {
        Object routingKeysObj = properties.getHeader("routing_keys");
        if (routingKeysObj instanceof ArrayList) {
            ArrayList<?> routingKeys = (ArrayList<?>) routingKeysObj;
            if (routingKeys.size() > 0) {
                Object routingKeyObj = routingKeys.get(0);
                if (routingKeyObj instanceof String) {
                    return (String) routingKeyObj;
                }
            }
        }
        return "unknown";
    }
}
