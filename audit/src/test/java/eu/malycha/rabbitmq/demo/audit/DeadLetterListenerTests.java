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
public class DeadLetterListenerTests {

    @Autowired
    TestRabbitTemplate rabbitTemplate;

    @Autowired
    TaskCounter taskCounter;

    @Autowired
    DeadLetterListener deadLetterListener;

    @Test
    void messageOnDeadLetterQueueShouldIncreaseCounter() {
        this.rabbitTemplate.convertAndSend("", "dead-letter", "Task");
        assertEquals(Map.of("dead-letter", 1), taskCounter.getCounters());
    }

    @Test
    void whenTaskPrefixMatchesCounterIsIncreased() {
        deadLetterListener.setTaskPrefix("RED");
        this.rabbitTemplate.convertAndSend("", "dead-letter", "RED-Task");
        assertEquals(Map.of("dead-letter", 1), taskCounter.getCounters());
    }

    @Test
    void whenTaskPrefixDoesntMatchCounterIsUnchanged() {
        deadLetterListener.setTaskPrefix("RED");
        this.rabbitTemplate.convertAndSend("", "dead-letter", "GREEN-Task");
        assertEquals(Collections.EMPTY_MAP, taskCounter.getCounters());
    }

}
