package eu.malycha.rabbitmq.demo.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class FirehoseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirehoseListener.class);

    private final TaskCounter taskCounter;

    @Value("${audit.task.prefix}")
    private String taskPrefix;

    public FirehoseListener(TaskCounter taskCounter) {
        this.taskCounter = taskCounter;
    }

    @RabbitListener(queues = "${audit.firehose.queue}", containerFactory = "auditContainerFactory")
    public void observe(String task, Message message) {
        if (!task.startsWith(taskPrefix)) {
            return;
        }
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
