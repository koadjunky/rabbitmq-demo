package eu.malycha.rabbitmq.demo.audit;

import eu.malycha.rabbitmq.demo.common.DeadLetterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class DeadLetterListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterListener.class);

    private final TaskCounter taskCounter;

    public DeadLetterListener(TaskCounter taskCounter) {
        this.taskCounter = taskCounter;
    }

    @RabbitListener(queues = DeadLetterConfiguration.TASK_DLQ,
            containerFactory = "auditContainerFactory")
    public void observe(String task) {
        taskCounter.inc(DeadLetterConfiguration.TASK_DLQ);
        LOGGER.info("Task submitted to exchange: {} ({})", DeadLetterConfiguration.TASK_DLX, task);
    }
}
