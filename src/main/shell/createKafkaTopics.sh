#!/bin/bash
docker_create_kafka="docker run --rm -it --net=host confluentinc/cp-kafka kafka-topics --zookeeper 127.0.0.1:2181 --create --topic "
confluent_path="/Users/e1xx/Downloads/confluent-5.4.1/bin"

function usage(){
  echo -e "\n \t for instance : bash $0 topic_name"
}

function createTopic(){
  local _topic="${1}"
  local _cmd="${confluent_path}/kafka-topics --zookeeper 127.0.0.1:2181 --create --topic ${_topic}
  --partitions 1 --replication-factor 1"
#  local _cmd="${docker_create_kafka} ${_topic} --partitions 1 --replication-factor 1"
  echo ${_cmd}
  eval ${_cmd}
}

topics=(
"customer_primitive_avro"
"customer_complex_avro"
)

for topic in ${topics[@]}; do
 createTopic ${topic}
done
