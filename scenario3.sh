#!/bin/bash

./gradlew clean assemble
docker-compose up &
sleep 15
docker-compose exec -T rabbitmq rabbitmqctl trace_on
java -jar audit/build/libs/audit.jar &
sleep 5
java -jar worker/build/libs/worker.jar --worker.processing-time=8000 > /dev/null 2>&1 &
sleep 5
java -jar worker/build/libs/worker.jar --worker.processing-time=8000 > /dev/null 2>&1 &
sleep 5
java -jar worker/build/libs/worker.jar --worker.processing-time=8000 > /dev/null 2>&1 &
sleep 5
java -jar producer/build/libs/producer.jar > /dev/null 2>&1 &
sleep 5
java -jar producer/build/libs/producer.jar --server.port=8092 > /dev/null 2>&1 &
sleep 5
java -jar producer/build/libs/producer.jar --server.port=8093 > /dev/null 2>&1 &
sleep 10
curl -X PUT -d enabled=1 http://localhost:8091/api/enabled &
curl -X PUT -d enabled=1 http://localhost:8092/api/enabled &
curl -X PUT -d enabled=1 http://localhost:8093/api/enabled &
sleep 30
curl -X PUT -d enabled=0 http://localhost:8091/api/enabled &
curl -X PUT -d enabled=0 http://localhost:8092/api/enabled &
curl -X PUT -d enabled=0 http://localhost:8093/api/enabled &
sleep 20
curl http://localhost:8081/api/counters
echo
kill $(jobs -p)
sleep 30
docker-compose down
