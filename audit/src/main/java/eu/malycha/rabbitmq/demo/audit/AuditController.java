package eu.malycha.rabbitmq.demo.audit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class AuditController {

    private final TaskCounter taskCounter;

    public AuditController(TaskCounter taskCounter) {
        this.taskCounter = taskCounter;
    }

    @GetMapping("/api/counters")
    public Map<String, Integer> getCounters() {
        return taskCounter.getCounters();
    }

}
