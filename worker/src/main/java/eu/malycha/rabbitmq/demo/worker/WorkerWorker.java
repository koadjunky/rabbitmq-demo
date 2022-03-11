package eu.malycha.rabbitmq.demo.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkerWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerWorker.class);

    private final AmqpTemplate workInboundTemplate;
    private final AmqpTemplate workOutboundTemplate;
    //TODO: Make configurable
    private final int processingTime = 1000;

    public WorkerWorker(AmqpTemplate workInboundTemplate, AmqpTemplate workOutboundTemplate) {
        this.workInboundTemplate = workInboundTemplate;
        this.workOutboundTemplate = workOutboundTemplate;
    }

    @Scheduled(fixedDelay = 100)
    public void process() {
        String message = (String) workInboundTemplate.receiveAndConvert();
        if (message != null) {
            Thread.sleep(processingTime);
            workOutboundTemplate.convertAndSend(message + "-processed");
            LOGGER.info("Message processed");
        }
    }

}
