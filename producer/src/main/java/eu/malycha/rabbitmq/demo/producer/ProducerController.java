package eu.malycha.rabbitmq.demo.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    private final ProducerWorker producerWorker;

    public ProducerController(ProducerWorker producerWorker) {
        this.producerWorker = producerWorker;
    }

    @GetMapping("/api/enabled")
    public String isEnabled() {
        return producerWorker.isEnabled() ? "enabled" : "disabled";
    }

    @PutMapping("/api/enabled")
    public void setEnabled(@RequestParam Integer enabled) {
        producerWorker.setEnabled(0 != enabled);
    }

}
