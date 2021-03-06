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

    @Value("${worker.fail}")
    private boolean failTasks;

    private int counter;

    public WorkerListener(AmqpTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = DemoConfiguration.WORK_INBOUND)
    public void process(String task,
                        Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                        @Header("x-expiration-time") Long expiration)
            throws Exception {
        if (expiration > System.currentTimeMillis() + processingTime) {
            processFresh(task, channel, tag);
        } else {
            processStale(task, channel, tag);
        }
    }

    private void processFresh(String task, Channel channel, long tag) throws IOException {
        try {
            attemptFail();
            Thread.sleep(processingTime);
            String processed = task + "-processed";
            template.convertAndSend("", DemoConfiguration.WORK_OUTBOUND, processed);
            LOGGER.info("Task processed: {}", processed);
            channel.basicAck(tag, false);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            // Requeue task for another processing attempt
            LOGGER.warn("Task processing interrupted: {}", task);
            channel.basicReject(tag, true);
        } catch (Exception ex) {
            // Requeue task for another processing attempt
            LOGGER.warn("Task processing failed: {}", task);
            channel.basicReject(tag, true);
        }
    }

    private void processStale(String task, Channel channel, long tag) throws IOException {
        // Send task to dead letter exchange
        LOGGER.info("Task rejected: {}", task);
        channel.basicReject(tag, false);
    }

    private void attemptFail() throws Exception {
        // Fail every second attempt if failTasks is true
        counter = (counter + 1) % 2;
        if (failTasks && counter == 0) {
            throw new Exception("Processing failure.");
        }
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public void setFailTasks(boolean failTasks) {
        this.failTasks = failTasks;
        this.counter = 0;
    }
}
