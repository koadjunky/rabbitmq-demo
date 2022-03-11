package eu.malycha.rabbitmq.demo.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;


public class CertifierHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertifierHandler.class);

    private final AmqpTemplate amqpTemplate;

    public CertifierHandler(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void handleMessage(String text) {
        amqpTemplate.convertAndSend(text + "-certified");
        LOGGER.info("Certified message");
    }
}
