package eu.malycha.rabbitmq.demo.worker;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfiguration {

    final String workInboundQueueName = "work-inbound";
    final String workOutboundQueueName = "work-outbound";

    @Bean
    public RabbitTemplate workInboundTemplate(ConnectionFactory connectionFactory, AmqpTemplate workOutboundTemplate) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setRoutingKey(workInboundQueueName);
        template.setDefaultReceiveQueue(workOutboundQueueName);
        return template;
    }

    @Bean
    public RabbitTemplate workOutboundTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setRoutingKey(workOutboundQueueName);
        template.setDefaultReceiveQueue(workOutboundQueueName);
        template.receiveAndConvert()
        return template;
    }
}
