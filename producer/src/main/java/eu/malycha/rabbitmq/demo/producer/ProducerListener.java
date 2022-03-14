package eu.malycha.rabbitmq.demo.producer;

import com.rabbitmq.client.Channel;
import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ProducerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerListener.class);

    private final AmqpTemplate template;

    public ProducerListener(AmqpTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = DemoConfiguration.WORK_OUTBOUND)
    public void certify(String task, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            String certified = task + "-certified";
            template.convertAndSend(DemoConfiguration.CERTIFIED_RESULT, certified);
            LOGGER.info("Task certified: {}", certified);
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            LOGGER.warn("Task certification failed: {}", task);
            channel.basicReject(tag, true);
        }
    }
}
