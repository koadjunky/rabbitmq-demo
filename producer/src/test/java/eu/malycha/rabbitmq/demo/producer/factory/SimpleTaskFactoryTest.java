package eu.malycha.rabbitmq.demo.producer.factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class SimpleTaskFactoryTest {

    @Test
    void tasksAreUnique() {
        int number = 10;
        TaskFactory taskFactory = new SimpleTaskFactory();
        long count = IntStream.rangeClosed(1, number)
                .boxed()
                .map(x -> taskFactory.produce())
                .distinct()
                .count();
        Assertions.assertEquals(number, count);
    }
}
