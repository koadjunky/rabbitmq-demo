package eu.malycha.rabbitmq.demo.producer.factory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile("tricolor")
public class TricolorTaskFactory implements TaskFactory {

    private static final List<String> colors = List.of("RED-", "BLUE-", "GREEN-");

    private int index;

    @Override
    public String produce() {
        return nextColor() + UUID.randomUUID().toString();
    }

    private String nextColor() {
        String color = colors.get(index);
        index = (index + 1) % colors.size();
        return color;
    }
}
