# rabbitmq-demo

docker-compose exec -T rabbitmq rabbitmqctl trace_on

./gradlew :audit:bootRun --args='--spring.profiles.active=tricolor,red --server.port=8081'
./gradlew :producer:bootRun --args='--spring.profiles.active=tricolor'
./gradlew :worker:bootRun --args='--spring.profiles.active=tricolor'


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

