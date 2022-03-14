package eu.malycha.rabbitmq.demo.producer.factory;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class TricolorTaskFactoryTest {

    @Test
    void tasksAreUnique() {
        int number = 30;
        TaskFactory taskFactory = new TricolorTaskFactory();
        long count = IntStream.rangeClosed(1, number)
                .boxed()
                .map(x -> taskFactory.produce())
                .distinct()
                .count();
        Assertions.assertEquals(number, count);
    }

    @Test
    void tasksStartWithRedGreenBluePrefix() {
        int number = 30;
        TaskFactory taskFactory = new TricolorTaskFactory();
        Set<String> prefixes = IntStream.rangeClosed(1, number)
                .boxed()
                .map(x -> taskFactory.produce())
                .map(x -> x.split("-")[0])
                .collect(Collectors.toSet());
        Assertions.assertEquals(Sets.newHashSet("RED", "GREEN", "BLUE"), prefixes);
    }

}
