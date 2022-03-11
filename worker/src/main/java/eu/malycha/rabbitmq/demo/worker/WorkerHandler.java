package eu.malycha.rabbitmq.demo.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;


public class WorkerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerHandler.class);

    private final AmqpTemplate amqpTemplate;

    public WorkerHandler(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void handleMessage(String text) {
        amqpTemplate.convertAndSend(text + "-processed");
        LOGGER.info("Processed message");
    }
}
