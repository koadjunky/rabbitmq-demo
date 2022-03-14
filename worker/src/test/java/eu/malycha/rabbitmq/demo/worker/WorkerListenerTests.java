package eu.malycha.rabbitmq.demo.worker;

import com.rabbitmq.client.Channel;
import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;


@SpringBootTest
class WorkerListenerTests {

    @Autowired
    RabbitListenerTestHarness harness;

    @Autowired
    TestRabbitTemplate rabbitTemplate;

    @Autowired
    Channel channel;

    @Autowired
    WorkerListener workerListener;

    @AfterEach
    void tearDown() {
        workerListener.setFailTasks(false);
        reset(channel);
    }

    @Test
    void workerShouldProcessNonExpiredTask() throws Exception {
        this.rabbitTemplate.convertAndSend("", DemoConfiguration.WORK_INBOUND, "Task", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryTag(1);
            properties.setExpiration("10000");
            properties.setHeader("x-expiration-time", System.currentTimeMillis() + 10000);
            return message;
        });
        RabbitListenerTestHarness.InvocationData invocationData =
                this.harness.getNextInvocationDataFor(DemoConfiguration.WORK_OUTBOUND, 10, TimeUnit.SECONDS);
        assertNotNull(invocationData);
        assertEquals("Task-processed", invocationData.getArguments()[0]);
        verify(channel).basicAck(1, false);
    }

    @Test
    void workerShouldDiscardExpiredTask() throws Exception {
        this.rabbitTemplate.convertAndSend("", DemoConfiguration.WORK_INBOUND, "Task", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryTag(1);
            properties.setExpiration("10000");
            properties.setHeader("x-expiration-time", System.currentTimeMillis() - 10000);
            return message;
        });
        RabbitListenerTestHarness.InvocationData invocationData =
                this.harness.getNextInvocationDataFor(DemoConfiguration.WORK_OUTBOUND, 1, TimeUnit.SECONDS);
        assertNull(invocationData);
        verify(channel).basicReject(1, false);
    }

    @Test
    void workerShouldFailEverySecondTaskInFailMode() throws Exception {
        workerListener.setFailTasks(true);
        this.rabbitTemplate.convertAndSend("", DemoConfiguration.WORK_INBOUND, "Task_1", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryTag(1);
            properties.setExpiration("10000");
            properties.setHeader("x-expiration-time", System.currentTimeMillis() + 10000);
            return message;
        });
        RabbitListenerTestHarness.InvocationData invocationData =
                this.harness.getNextInvocationDataFor(DemoConfiguration.WORK_OUTBOUND, 10, TimeUnit.SECONDS);
        assertNotNull(invocationData);
        assertEquals("Task_1-processed", invocationData.getArguments()[0]);
        verify(channel).basicAck(1, false);

        this.rabbitTemplate.convertAndSend("", DemoConfiguration.WORK_INBOUND, "Task_2", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryTag(1);
            properties.setExpiration("10000");
            properties.setHeader("x-expiration-time", System.currentTimeMillis() + 10000);
            return message;
        });
        invocationData = this.harness.getNextInvocationDataFor(DemoConfiguration.WORK_OUTBOUND, 1, TimeUnit.SECONDS);
        assertNull(invocationData);
        verify(channel).basicReject(1, true);
    }

}
