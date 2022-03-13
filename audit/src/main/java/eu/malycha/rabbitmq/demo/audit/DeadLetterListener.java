package eu.malycha.rabbitmq.demo.audit;

import eu.malycha.rabbitmq.demo.common.SimpleDeadLetterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DeadLetterListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterListener.class);

    private final TaskCounter taskCounter;

    @Value("${audit.task.prefix}")
    private String taskPrefix;

    public DeadLetterListener(TaskCounter taskCounter) {
        this.taskCounter = taskCounter;
    }

    @RabbitListener(queues = "${audit.deadletter.queue}",
            containerFactory = "auditContainerFactory")
    public void observe(String task) {
        if (!task.startsWith(taskPrefix)) {
            return;
        }
        taskCounter.inc(SimpleDeadLetterConfiguration.TASK_DLQ);
        LOGGER.info("Task submitted to exchange: {} ({})", SimpleDeadLetterConfiguration.TASK_DLX, task);
    }
}
