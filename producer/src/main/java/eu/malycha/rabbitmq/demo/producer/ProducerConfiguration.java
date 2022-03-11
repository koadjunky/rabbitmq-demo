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

    final String workInboundQueueName = "work-inbound";
    final String workOutboundQueueName = "work-outbound";
    final String certifiedResultQueueName = "certified-result";

    @Bean
    public RabbitTemplate workInboundTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setRoutingKey(workInboundQueueName);
        template.setDefaultReceiveQueue(workInboundQueueName);
        return template;
    }

    @Bean
    public RabbitTemplate certifiedResultTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setRoutingKey(certifiedResultQueueName);
        template.setDefaultReceiveQueue(certifiedResultQueueName);
        return template;
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
    public Queue workInboundQueue() {
        return new Queue(workInboundQueueName);
    }

    @Bean
    public Queue workOutboundQueue() {
        return new Queue(workOutboundQueueName);
    }

    @Bean
    public Queue certifiedResultQueueName() {
        return new Queue(certifiedResultQueueName);
    }
}
