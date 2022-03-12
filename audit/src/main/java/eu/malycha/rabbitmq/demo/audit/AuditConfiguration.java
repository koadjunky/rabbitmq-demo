package eu.malycha.rabbitmq.demo.audit;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfiguration {

    //TODO: Other constants convention
    public static final String FIREHOSE_QUEUE = "firehose-queue";

    @Bean
    public TopicExchange firehoseExchange() {
        TopicExchange exchange = new TopicExchange("amq.rabbitmq.trace");
        exchange.setInternal(true);
        return exchange;
    }

    @Bean
    public Queue firehoseQueue() {
        return new Queue(FIREHOSE_QUEUE, true, true, true, null);
    }

    @Bean
    public Binding firehoseBinding() {
        return BindingBuilder.bind(firehoseQueue())
                .to(firehoseExchange())
                .with("publish.#");
    }
}
