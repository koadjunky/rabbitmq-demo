package eu.malycha.rabbitmq.demo.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("tricolor")
public class TricolorDeadLetterConfiguration {

    public static final String TASK_DLX = "dead-letter";
    public static final String RED_TASK_DLQ = "red-dead-letter";
    public static final String GREEN_TASK_DLQ = "green-dead-letter";
    public static final String BLUE_TASK_DLQ = "blue-dead-letter";

    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(TASK_DLX);
    }

    @Bean
    Queue redDeadLetterQueue() {
        return QueueBuilder.nonDurable(RED_TASK_DLQ).build();
    }

    @Bean
    Queue greenDeadLetterQueue() {
        return QueueBuilder.nonDurable(GREEN_TASK_DLQ).build();
    }

    @Bean
    Queue blueDeadLetterQueue() {
        return QueueBuilder.nonDurable(BLUE_TASK_DLQ).build();
    }

    @Bean
    Binding redDeadLetterBinding() {
        return BindingBuilder.bind(redDeadLetterQueue())
                .to(deadLetterExchange());
    }

    @Bean
    Binding greenDeadLetterBinding() {
        return BindingBuilder.bind(greenDeadLetterQueue())
                .to(deadLetterExchange());
    }

    @Bean
    Binding blueDeadLetterBinding() {
        return BindingBuilder.bind(blueDeadLetterQueue())
                .to(deadLetterExchange());
    }
}
