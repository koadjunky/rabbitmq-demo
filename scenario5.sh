#!/bin/bash

./gradlew clean assemble
docker-compose up &
sleep 15
docker-compose exec -T rabbitmq rabbitmqctl trace_on
java -jar audit/build/libs/audit.jar --spring.profiles.active=tricolor,red --server.port=8081 > red.txt 2>&1 &
sleep 5
java -jar audit/build/libs/audit.jar --spring.profiles.active=tricolor,green --server.port=8082 > green.txt 2>&1 &
sleep 5
java -jar audit/build/libs/audit.jar --spring.profiles.active=tricolor,blue --server.port=8083 > blue.txt 2>&1 &
sleep 5
java -jar worker/build/libs/worker.jar --spring.profiles.active=tricolor > /dev/null 2>&1 &
sleep 5
java -jar producer/build/libs/producer.jar --spring.profiles.active=tricolor > /dev/null 2>&1 &
sleep 10
curl -X PUT -d enabled=1 http://localhost:8091/api/enabled &
sleep 30
curl -X PUT -d enabled=0 http://localhost:8091/api/enabled &
sleep 20
echo "RED counters"
curl http://localhost:8081/api/counters
echo
echo "GREEN counters"
curl http://localhost:8082/api/counters
echo
echo "BLUE counters"
curl http://localhost:8083/api/counters
echo
echo "RED log"
cat red.txt
echo "GREEN log"
cat green.txt
echo "BLUE log"
cat blue.txt
kill $(jobs -p)
sleep 30
docker-compose down
