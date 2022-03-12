package eu.malycha.rabbitmq.demo.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkerWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerWorker.class);

    private final AmqpTemplate workInboundTemplate;
    private final AmqpTemplate workOutboundTemplate;

    @Value("${worker.processing-time}")
    private int processingTime;

    public WorkerWorker(AmqpTemplate workInboundTemplate, AmqpTemplate workOutboundTemplate) {
        this.workInboundTemplate = workInboundTemplate;
        this.workOutboundTemplate = workOutboundTemplate;
    }

    @Scheduled(fixedRate = 100)
    public void process() throws InterruptedException {
        String message = (String) workInboundTemplate.receiveAndConvert(WorkerConfiguration.workInboundQueueName);
        if (message != null) {
            Thread.sleep(processingTime);
            workOutboundTemplate.convertAndSend(WorkerConfiguration.workOutboundQueueName, message + "-processed");
            LOGGER.info("Message processed");
        }
    }

}
