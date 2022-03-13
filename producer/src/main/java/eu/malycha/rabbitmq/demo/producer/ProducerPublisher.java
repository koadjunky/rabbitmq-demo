package eu.malycha.rabbitmq.demo.producer;

import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class ProducerPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerPublisher.class);

    private final AmqpTemplate amqpTemplate;
    private boolean enabled = true;
    private final long ttl = 10000;

    public ProducerPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Scheduled(fixedRateString = "${producer.rate}")
    public void produce() {
        if (enabled) {
            UUID uuid = UUID.randomUUID();
            String task = uuid.toString();
            amqpTemplate.convertAndSend("", DemoConfiguration.WORK_INBOUND, task, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties = message.getMessageProperties();
                    properties.setExpiration(String.valueOf(ttl));
                    properties.setHeader("x-expiration-time", System.currentTimeMillis() + ttl);
                    return message;
                }
            });
            LOGGER.info("Task produced: {}", task);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
