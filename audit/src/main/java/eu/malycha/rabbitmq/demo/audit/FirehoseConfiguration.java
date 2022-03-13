package eu.malycha.rabbitmq.demo.audit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirehoseConfiguration {

    public static final String FIREHOSE_EXCHANGE = "amq.rabbitmq.trace";
    public static final String FIREHOSE_QUEUE = "firehose-queue";
    public static final String PUBLISH_RKEY = "publish.#";

    @Bean
    public TopicExchange firehoseExchange() {
        TopicExchange exchange = new TopicExchange(FIREHOSE_EXCHANGE);
        exchange.setInternal(true);
        return exchange;
    }

    @Bean
    public Queue firehoseQueue() {
        return QueueBuilder.nonDurable(FIREHOSE_QUEUE).build();
    }

    @Bean
    public Binding firehoseBinding() {
        return BindingBuilder.bind(firehoseQueue())
                .to(firehoseExchange())
                .with(PUBLISH_RKEY);
    }
}
