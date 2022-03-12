package eu.malycha.rabbitmq.demo.producer;

import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfiguration {

    @Bean
    public SimpleMessageListenerContainer workOutboundListener(ConnectionFactory connectionFactory,
                                                               AmqpTemplate template) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(DemoConfiguration.WORK_OUTBOUND);
        container.setMessageListener(new MessageListenerAdapter(new CertifierHandler(template)));
        return container;
    }

}
