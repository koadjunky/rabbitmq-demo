package eu.malycha.rabbitmq.demo.audit;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FirehoseListenerTests {

    @Autowired
    TestRabbitTemplate rabbitTemplate;

    @Autowired
    TaskCounter taskCounter;

    @Autowired
    FirehoseListener firehoseListener;

    @Test
    void messageOnFirehoseQueueShouldIncreaseCounter() {
        this.rabbitTemplate.convertAndSend("", "firehose-queue", "Task", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setHeader("routing_keys", new ArrayList<>(List.of("work-inbound")));
            return message;
        });
        assertEquals(Map.of("work-inbound", 1), taskCounter.getCounters());
    }

    @Test
    void whenNoRoutingKeysPresentUnknownCounterIsIncremented() {
        this.rabbitTemplate.convertAndSend("", "firehose-queue", "Task");
        assertEquals(Map.of("unknown", 1), taskCounter.getCounters());
    }

    @Test
    void whenTaskPrefixMatchesCounterIsIncreased() {
        firehoseListener.setTaskPrefix("RED");
        this.rabbitTemplate.convertAndSend("", "firehose-queue", "RED-Task", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setHeader("routing_keys", new ArrayList<>(List.of("work-inbound")));
            return message;
        });
        assertEquals(Map.of("work-inbound", 1), taskCounter.getCounters());
    }

    @Test
    void whenTaskPrefixDoesntMatchCounterIsUnchanged() {
        firehoseListener.setTaskPrefix("RED");
        this.rabbitTemplate.convertAndSend("", "firehose-queue", "GREEN-Task", message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setHeader("routing_keys", new ArrayList<>(List.of("work-inbound")));
            return message;
        });
        assertEquals(Collections.EMPTY_MAP, taskCounter.getCounters());
    }

}
