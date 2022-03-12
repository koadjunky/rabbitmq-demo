package eu.malycha.rabbitmq.demo.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfiguration {

    public static final String workInboundQueueName = "work-inbound";
    public static final String workOutboundQueueName = "work-outbound";
    public static final String certifiedResultQueueName = "certified-result";

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer workOutboundListener(ConnectionFactory connectionFactory, AmqpTemplate certifiedResultTemplate) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(workOutboundQueueName);
        container.setMessageListener(new MessageListenerAdapter(new CertifierHandler(certifiedResultTemplate)));
        return container;
    }

    @Bean
    public Queue inboundQueue() {
        return new Queue(workInboundQueueName);
    }

    @Bean
    public Queue outboundQueue() {
        return new Queue(workOutboundQueueName);
    }

    @Bean
    public Queue certifiedResultQueue() {
        return new Queue(certifiedResultQueueName);
    }
}
