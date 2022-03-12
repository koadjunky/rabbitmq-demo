package eu.malycha.rabbitmq.demo.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO: Worker nie potrzebuje RESTa
@SpringBootApplication
@EnableScheduling
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

}
