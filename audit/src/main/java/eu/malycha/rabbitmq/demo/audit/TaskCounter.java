package eu.malycha.rabbitmq.demo.audit;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Component
public class TaskCounter {

    private final Map<String, Integer> counters = new HashMap<>();

    public synchronized void inc(String queue) {
        counters.putIfAbsent(queue, 0);
        counters.computeIfPresent(queue, (key, value) -> value + 1);
    }

    public Map<String, Integer> getCounters() {
        return Collections.unmodifiableMap(counters);
    }
}
