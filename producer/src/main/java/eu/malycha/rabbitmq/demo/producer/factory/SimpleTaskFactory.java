package eu.malycha.rabbitmq.demo.producer.factory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("!tricolor")
public class SimpleTaskFactory implements TaskFactory {

    @Override
    public String produce() {
        return UUID.randomUUID().toString();
    }
}
