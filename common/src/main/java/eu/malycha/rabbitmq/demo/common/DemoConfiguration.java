package eu.malycha.rabbitmq.demo.common;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DemoConfiguration implements RabbitListenerConfigurer {

    public static final String WORK_INBOUND = "work-inbound";
    public static final String WORK_OUTBOUND = "work-outbound";
    public static final String CERTIFIED_RESULT = "certified-result";

    @Bean
    public ConnectionFactory taskConnectionFactory() {
        return new CachingConnectionFactory();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory taskContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(taskConnectionFactory());
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public RabbitTemplate taskTemplate() {
        return new RabbitTemplate(taskConnectionFactory());
    }

    @Bean
    public Queue inboundQueue() {
        return buildQueue(WORK_INBOUND);
    }

    @Bean
    public Queue outboundQueue() {
        return buildQueue(WORK_OUTBOUND);
    }

    @Bean
    public Queue certifiedResultQueue() {
        return buildQueue(CERTIFIED_RESULT);
    }

    private static Queue buildQueue(String name) {
        return QueueBuilder.nonDurable(name)
                .deadLetterExchange(SimpleDeadLetterConfiguration.TASK_DLX)
                .deadLetterRoutingKey(SimpleDeadLetterConfiguration.TASK_DLQ)
                .build();
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setContainerFactory(taskContainerFactory());
    }
}
