#kafka-ksql-jdbc-sink
==============================================================================

kafka-ksql-jdbc-sink project provides utilities to
1. produce data to kafka
2. Run the connector to read data from kafka to minio s3
3. FRom kafka to postgres using kafka-jdbc-connector

Kafka to s3 connector
==============================================================================

# Prerequisites
gradle and mc installed.
brew install gradle
brew install minio/stable/mc
# 1. Clone the project
clone the project https://github.com/mahen-github/kafka-ksql-jdbc-sink.git

git clone https://github.com/mahen-github/kafka-ksql-jdbc-sink.git
# 2. Build the project
cd kafka-ksql-jdbc-sink

gradle clean build;
docker-compose up
# 3. Run docker compose
docker-compose up
running docker containers
# 4. Create kafka topics
docker run --rm -it --net=host confluentinc/cp-kafka kafka-topics --zookeeper 127.0.0.1:2181 --create --topic customer --partitions 1 --replication-factor 1
 java -cp  build/libs/kafka-ksql-jdbc-sink-1.0-SNAPSHOT.jar com.mahendran.kafka.CustomerProducer

# 5. Run the event producer
java -cp  build/libs/kafka-ksql-jdbc-sink-1.0-SNAPSHOT.jar com.mahendran.kafka.CustomerProducer
docker run - rm -it - net=host confluentinc/cp-kafka kafka-console-consumer - bootstrap-server 127.0.0.1:9092 - topic customer - from-beginning
mc config host add myminio http://localhost:9000 ksink-key IEZIE4NEANEV4VISNI6QUAUKYAHNAI2B
## 5.1 verify
docker run - rm -it - net=host confluentinc/cp-kafka kafka-console-consumer - bootstrap-server 127.0.0.1:9092 - topic customer - from-beginning
avro data posted to the customer topic
# 6. Create bucket
mc config host add myminio http://localhost:9000 ksink-key IEZIE4NEANEV4VISNI6QUAUKYAHNAI2B
mc mb --region us-west-2 myminio/customer-bucket
mc policy public myminio/customer-bucket
## 6.1 verify
mc ls myminio
mc lists the bucket created

# 7. Post the connector config
curl -s -X POST   -H "Content-type: application/json"   -d @connectors/customer-s3-sink.json  http://localhost:28082/connectors/
connector api returns config after successful post
## 7.1 verify
mc ls myminio/customer-bucket/topics/customer/partition=0/customer+0+0000000000.avro

Avro console consumer
=====================
~/confluent-5.4.1/bin/kafka-avro-console-consumer --bootstrap-server localhost:9092 \
                                --property schema.registry.url=http://localhost:8081 \
                                --topic customer \
                                --from-beginning