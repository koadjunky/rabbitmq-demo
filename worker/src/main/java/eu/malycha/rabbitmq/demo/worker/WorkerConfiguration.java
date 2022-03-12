package eu.malycha.rabbitmq.demo.worker;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfiguration {

    public static final String workInboundQueueName = "work-inbound";
    public static final String workOutboundQueueName = "work-outbound";

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Queue inboundQueue() {
        return new Queue(WorkerConfiguration.workInboundQueueName);
    }

    @Bean
    public Queue outboundQueue() {
        return new Queue(WorkerConfiguration.workOutboundQueueName);
    }

}
