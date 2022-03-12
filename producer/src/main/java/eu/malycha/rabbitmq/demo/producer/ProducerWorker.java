package eu.malycha.rabbitmq.demo.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class ProducerWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerWorker.class);

    private final AmqpTemplate amqpTemplate;
    private boolean enabled = true;

    public ProducerWorker(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Scheduled(fixedRateString = "${producer.rate}")
    public void produce() {
        if (enabled) {
            UUID uuid = UUID.randomUUID();
            amqpTemplate.convertAndSend(ProducerConfiguration.workInboundQueueName, uuid.toString());
            LOGGER.info("Produced message");
        } else {
            LOGGER.info("Producer not enabled");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
