package eu.malycha.rabbitmq.demo.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class AuditListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditListener.class);

    @RabbitListener(queues = AuditConfiguration.FIREHOSE_QUEUE)
    public void observe(Message message) {
        MessageProperties properties = message.getMessageProperties();
        // TODO: Make it safe
        String routingKey = (String) ((ArrayList) properties.getHeader("routing_keys")).get(0);
        LOGGER.info("Message to exchange: {}", routingKey);
    }
}
