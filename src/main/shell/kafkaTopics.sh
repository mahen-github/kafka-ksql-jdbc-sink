#!/bin/bash
docker_create_kafka="docker run --rm -it --net=host confluentinc/cp-kafka kafka-topics --zookeeper 127.0.0.1:2181 --create --topic "

docker_delete_kafka="docker run --rm -it --net=host confluentinc/cp-kafka kafka-topics --zookeeper 127.0.0.1:2181 --delete --topic "

function usage(){
  echo -e "
  ${RED}
    Missing arguments :
    To create topics : bash $0 createTopic
    To delete topics : bash $0 deleteTopic
   ${NC}
   "
}

function usage(){
  echo -e "\n \t for instance : bash $0 topic_name"
}

function createTopic(){
  local _topic="${1}"
  local _cmd="${docker_create_kafka} ${_topic} --partitions 1 --replication-factor 1"
  echo ${_cmd}
  eval ${_cmd}
}

function deleteTopic(){
  local _topic="${1}"
  local _cmd="${docker_delete_kafka} ${_topic}"
  echo ${_cmd}
  eval ${_cmd}
}

topics=(
"customer"
)

[[ $# -eq 0 ]] && { usage; exit 0; }

if [[ $1 == "createTopic" ]]; then
  for topic in ${topics[@]}; do
    createTopic ${topic}
  done
fi

if [[ $1 == "deleteTopic" ]]; then
  for topic in ${topics[@]}; do
    deleteTopic ${topic}
  done
fi

//docker run --rm -it --net=host confluentinc/cp-kafka kafka-topics --zookeeper 127.0.0.1:2181 --create --topic customer --partitions 1 --replication-factor 1