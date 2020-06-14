#!/usr/bin/env bash

echo Starting register schemas...
./src/main/shell/registerSchemas.sh
echo Starting create Kafka topics...
./src/main/shell/createKafkaTopics.sh

