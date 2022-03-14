package eu.malycha.rabbitmq.demo.producer;

import eu.malycha.rabbitmq.demo.common.DemoConfiguration;
import eu.malycha.rabbitmq.demo.producer.factory.TaskFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProducerPublisherTests {

    @Mock
    TaskFactory factory;

    @Mock
    AmqpTemplate template;

    @Captor
    ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor;

    @Mock
    Message message;

    @Mock
    MessageProperties messageProperties;

    ProducerPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new ProducerPublisher(template, factory);
        publisher.setEnabled(true);
    }

    @Test
    void publisherShouldSendMessageIfEnabled() {
        when(factory.produce()).thenReturn("Task");
        publisher.setEnabled(true);
        publisher.produce();
        assertTrue(publisher.isEnabled());
        verify(template, times(1))
                .convertAndSend(eq(""), eq(DemoConfiguration.WORK_INBOUND), eq("Task"), any(MessagePostProcessor.class));
    }

    @Test
    void publisherShouldNotSendMessageIfDisabled() {
        publisher.setEnabled(false);
        publisher.produce();
        assertFalse(publisher.isEnabled());
        verifyNoInteractions(factory);
        verifyNoInteractions(template);
    }

    @Test
    void publisherShouldSetHeaders() {
        when(factory.produce()).thenReturn("Task");
        when(message.getMessageProperties()).thenReturn(messageProperties);
        publisher.produce();
        verify(template, times(1))
                .convertAndSend(anyString(), anyString(), anyString(), messagePostProcessorCaptor.capture());
        messagePostProcessorCaptor.getValue().postProcessMessage(message);
        verify(messageProperties).setExpiration("10000");
        verify(messageProperties).setHeader(eq("x-expiration-time"), anyLong());
    }
}
