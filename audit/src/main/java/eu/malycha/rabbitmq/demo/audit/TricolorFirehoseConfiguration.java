package eu.malycha.rabbitmq.demo.audit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("tricolor")
public class TricolorFirehoseConfiguration {

    public static final String FIREHOSE_EXCHANGE = "amq.rabbitmq.trace";
    public static final String RED_FIREHOSE_QUEUE = "red-firehose-queue";
    public static final String GREEN_FIREHOSE_QUEUE = "green-firehose-queue";
    public static final String BLUE_FIREHOSE_QUEUE = "blue-firehose-queue";
    public static final String PUBLISH_RKEY = "publish.#";

    @Bean
    public TopicExchange firehoseExchange() {
        TopicExchange exchange = new TopicExchange(FIREHOSE_EXCHANGE);
        exchange.setInternal(true);
        return exchange;
    }

    @Bean
    public Queue redFirehoseQueue() {
        return QueueBuilder.nonDurable(RED_FIREHOSE_QUEUE).build();
    }

    @Bean
    public Queue greenFirehoseQueue() {
        return QueueBuilder.nonDurable(GREEN_FIREHOSE_QUEUE).build();
    }

    @Bean
    public Queue blueFirehoseQueue() {
        return QueueBuilder.nonDurable(BLUE_FIREHOSE_QUEUE).build();
    }

    @Bean
    public Binding redFirehoseBinding() {
        return BindingBuilder.bind(redFirehoseQueue())
                .to(firehoseExchange())
                .with(PUBLISH_RKEY);
    }

    @Bean
    public Binding greenFirehoseBinding() {
        return BindingBuilder.bind(greenFirehoseQueue())
                .to(firehoseExchange())
                .with(PUBLISH_RKEY);
    }

    @Bean
    public Binding blueFirehoseBinding() {
        return BindingBuilder.bind(blueFirehoseQueue())
                .to(firehoseExchange())
                .with(PUBLISH_RKEY);
    }
}
