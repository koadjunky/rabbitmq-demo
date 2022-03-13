package eu.malycha.rabbitmq.demo.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DeadLetterConfiguration {

    public static final String TASK_DLX = "dead-letter";
    public static final String TASK_DLQ = "dead-letter";

    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(TASK_DLX);
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.nonDurable(TASK_DLQ).build();
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange());
    }
}
