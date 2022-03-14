# rabbitmq-demo

## Prerequisites

Tested on Linux Debian with OpenJdk.

* Java 11
* docker & docker-compose
* curl
* bash

## RabbitMQ usage

To prepare RabbitMQ for tests, start docker compose and turn on firehose feature:

```
docker-compose up
docker-compose exec -T rabbitmq rabbitmqctl trace_on
```

To reset queues between tests execute:

```
docker-compose down
```

## Building project

To build project, execute:

```
./gradlew clean assemble
```

## Command line parameters

### Producer

Producer can be started either from gradle:

```
./gradlew :producer:bootRun --args='<command_line_args>'
```

or directly from command line after building project:

```
java -jar producer/build/libs/producer.jar <command_line_parameters>
```

Producer accepts following command line parameters:

* ```--server.port=<port>``` - REST interface port, for running multiple producer instances, default is 8091
* ```--producer.rate=<miliseconds>``` - task generation rate in ms, default is 2000
* ```--spring.profiles.active=tricolor``` - configure queues for separate audit streams (Scenario 5)

Note: Producer is disabled after start - can be enabled via REST interface.

### Worker

Worker can be started either from gradle:

```
./gradlew :worker:bootRun --args='<command_line_args>'
```

or directly from command line after building project:

```
java -jar worker/build/libs/worker.jar <command_line_parameters>
```

Worker accepts following command line parameters:

* ```--worker.processing-time=<miliseconds>``` - task processing time in ms, default is 1000 (Scenario 3)
* ```--worker.fail=true``` - fail every second processed task (Scenario 4)
* ```--spring.profiles.active=tricolor``` - configure queues for separate audit streams (Scenario 5)

### Audit

Audit can be started either from gradle:

```
./gradlew :audit:bootRun --args='<command_line_args>'
```

or directly from command line after building project:

```
java -jar audit/build/libs/audit.jar <command_line_parameters>
```

Audit accepts following command line parameters:

* ```--server.port=<port>``` - REST interface port, for running multiple audit instances, default is 8081
* ```--spring.profiles.active=tricolor,red``` - configure queues for separate audit streams, observe RED stream (Scenario 5)
* ```--spring.profiles.active=tricolor,blue``` - configure queues for separate audit streams, observe BLUE stream (Scenario 5)
* ```--spring.profiles.active=tricolor,green``` - configure queues for separate audit streams, observe GREEN stream (Scenario 5)

Note: ```tricolor``` profile should be used either by all services or none.

## Web interfaces

Both Producer and Audit expose REST interfaces, Swagger is available on root path. Default
instances can be accessed via:
* Audit: http://localhost:8081/
* Producer: http://localhost:8091/

## Testing

Unit tests can be executed using:

```
./gradlew cleanTest test
```

## Scenarios

All scenarios can be executed running ```./scenarioX.sh``` bash script. Sleep values may
need adjustment for server speed.

Parts of log are silenced to expose most important information for given scenario (log is
still bit noisy through).

Scenario results:

### Scenario 1
* Produced tasks: 15
* Processed results: 15
* Certified results: 15
* Discarded results: 0

### Scenario 2
* Produced tasks: 45
* Processed results: 38
* Certified results: 38
* Discarded results: 7

### Scenario 3
* Produced tasks: 45
* Processed results: 12
* Certified results: 12
* Discarded results: 33

### Scenario 4
* Produced tasks: 15
* Processed results: 15
* Certified results: 15
* Discarded results: 0

### Scenario 5:
* RED counters
  * Produced tasks: 5
  * Processed results: 5
  * Certified results: 5
  * Discarded results: 0
* GREEN counters
  * Produced tasks: 5
  * Processed results: 5
  * Certified results: 5
  * Discarded results: 0
* BLUE counters
  * Produced tasks: 5
  * Processed results: 5
  * Certified results: 5
  * Discarded results: 0

## Solutions used

### RabbitMQ

* RabbitMQ is accessed using [Spring AMQP](https://docs.spring.io/spring-amqp/docs/current/reference/html/index.html) library from SpringBoot.
* Task processing is done on [Default Exchange](https://www.rabbitmq.com/tutorials/amqp-concepts.html#exchange-default)
* Queues are observed with [RabbitListener](https://docs.spring.io/spring-amqp/api/org/springframework/amqp/rabbit/annotation/RabbitListener.html)
* Task expiration is done using [RabbitMQ TTL](https://www.rabbitmq.com/ttl.html). Additionally,
  tasks expected to expire during processing are discarded when entering the Worker. There are other
  possible approaches:
  * interrupting processing with "alarm clock" thread - if processing time cannot be estimated,
  * propagating ttl down the worker-outbound queue back to Producer - can be done with ```x-expiration-time``` header
    already set by the Producer.
  
  Discarding tasks as soon as possible is considered best practice, thus my design decision.
* Worker discards tasks using [Manual Delivery Ack](https://www.rabbitmq.com/confirms.html). Same
  solution is used to put task back into queue in case of error.
* For even tasks distribution among single-threaded and lagging Workers, [Channel Prefetch](https://www.rabbitmq.com/confirms.html#channel-qos-prefetch)
  is limited to 1.
* Audit observes queues using [Firehose Tracer](https://www.rabbitmq.com/firehose.html) feature of RabbitMQ.
  Only publishing events are currently registered. There are alternative solutions:
  * Firehose can be emulated using Topic Exchange - creating copy of every message for Audit to observe
  * One of many external monitoring solutions
* Discarded tasks are observed using [Dead Letter Exchange](https://www.rabbitmq.com/dlx.html)
* Selective Audit is implemented using Fanout Exchange / Topic Exchange - each Audit exchange is observing own
  set of queues. Since RabbitMQ doesn't have content filtering in Exchange, it is done in Audit application.
  There is alternative solution - put COLOR in one of headers and filter using Headers Exchange, but it requires
  changes in Producer and Worker.
* Unit testing of Listeners is implemented using [RabbitListenerTest and RabbitListenerTestHarness](https://docs.spring.io/spring-amqp/docs/current/reference/html/index.html#test-harness)

### Spring

* Task generation is done using [Spring Task Scheduling](https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled)
* Configuration and command line parameters are done via [SpringBoot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config)
* Web interface is provided via [SpringBoot Web](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#web) and [Swagger](https://swagger.io/docs/specification/about/)
* Special configuration for Selective Audit (Scenario 5) is implemented using [SpringBoot Profiles](https://docs.spring.io/spring-boot/docs/1.2.0.M1/reference/html/boot-features-profiles.html)
* Mocking is done with [Mockito](https://github.com/mockito/mockito)
* REST testing is done with [Spring MVC Test Framework](https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/testing.html#spring-mvc-test-framework)
