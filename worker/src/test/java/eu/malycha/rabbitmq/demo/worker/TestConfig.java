package eu.malycha.rabbitmq.demo.worker;

import com.rabbitmq.client.Channel;
import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

// After: https://github.com/concurrent-recursion/spring-rabbit-test-example/blob/1fbadcd45a7ffe4dc3624703224a9c1df0f85586/src/test/java/com/example/junitstuff/TestConfig.java

@Configuration
@RabbitListenerTest(spy = false, capture = true)
public class TestConfig {

    @Primary
    @Bean
    public RabbitTemplate testRabbitTemplate(final ConnectionFactory connectionFactory) {
        return new TestRabbitTemplate(connectionFactory);
    }

    @Bean
    Channel mockChannel() {
        Channel channel = mock(Channel.class);
        given(channel.isOpen()).willReturn(true);
        return channel;
    }

    @Primary
    @Bean
    public ConnectionFactory mockConnectionFactory(Channel mockChannel) {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        willReturn(connection).given(factory).createConnection();
        willReturn(mockChannel).given(connection).createChannel(anyBoolean());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory mockConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(mockConnectionFactory);
        return factory;
    }

    @RabbitListener(id=DemoConfiguration.WORK_OUTBOUND, queues=DemoConfiguration.WORK_OUTBOUND)
    public void workOutbound(String task) {
        // Dummy listener, used to capture arguments
    }
}
