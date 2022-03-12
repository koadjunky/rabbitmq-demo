package eu.malycha.rabbitmq.demo.worker;

import com.rabbitmq.client.Channel;
import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class WorkerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerListener.class);

    private final AmqpTemplate template;

    @Value("${worker.processing-time}")
    private int processingTime;

    public WorkerListener(AmqpTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = DemoConfiguration.workInboundQueueName)
    public void process(String task,
                        Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                        @Header("x-expiration-time") Long expiration)
            throws InterruptedException, IOException {
        if (expiration > System.currentTimeMillis() + processingTime) {
            Thread.sleep(processingTime);
            template.convertAndSend("", DemoConfiguration.workOutboundQueueName, task + "-processed");
            channel.basicAck(tag, false);
            LOGGER.info("Task processed.");
        } else {
            channel.basicReject(tag, false);
            LOGGER.info("Task rejected.");
        }
    }

}
