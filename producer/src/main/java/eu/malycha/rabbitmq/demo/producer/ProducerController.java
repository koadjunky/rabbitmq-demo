package eu.malycha.rabbitmq.demo.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    private final ProducerPublisher producerPublisher;

    public ProducerController(ProducerPublisher producerPublisher) {
        this.producerPublisher = producerPublisher;
    }

    @GetMapping("/api/enabled")
    public String isEnabled() {
        return producerPublisher.isEnabled() ? "enabled" : "disabled";
    }

    @PutMapping("/api/enabled")
    public void setEnabled(@RequestParam Integer enabled) {
        producerPublisher.setEnabled(0 != enabled);
    }

}
