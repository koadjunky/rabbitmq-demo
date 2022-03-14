package eu.malycha.rabbitmq.demo.audit;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskCounterTest {

    @Test
    void incAddsOneToCounter() {
        TaskCounter taskCounter = new TaskCounter();
        taskCounter.inc("counter-1");
        taskCounter.inc("counter-2");
        taskCounter.inc("counter-1");
        assertEquals(Map.of("counter-1", 2, "counter-2", 1), taskCounter.getCounters());
    }

}
