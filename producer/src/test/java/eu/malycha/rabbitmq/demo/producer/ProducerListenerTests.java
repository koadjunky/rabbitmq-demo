package eu.malycha.rabbitmq.demo.producer;

import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class ProducerListenerTests {

    @Autowired
    RabbitListenerTestHarness harness;

    @Autowired
    TestRabbitTemplate rabbitTemplate;

    @Test
    void publisherShouldCertifyTask() throws InterruptedException {
        this.rabbitTemplate.convertAndSend("", DemoConfiguration.WORK_OUTBOUND, "Task", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryTag(1); // Workaround for TestRabbitTemplate not setting delivery tag
            return message;
        });
        RabbitListenerTestHarness.InvocationData invocationData =
                this.harness.getNextInvocationDataFor(DemoConfiguration.CERTIFIED_RESULT, 10, TimeUnit.SECONDS);
        assertNotNull(invocationData);
        assertEquals("Task-certified", invocationData.getArguments()[0]);
    }
}

