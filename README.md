#kafka-ksql-to-sink
==============================================================================

kafka-ksql-to-sink project provides utilities to produce data to kafka and to postgres using
kafka-jdbc-connector


Avro console consumer
=====================
/Users/e1xx/Downloads/confluent-5.4.1/bin/kafka-avro-console-consumer --bootstrap-server localhost:9092 \
                                --property schema.registry.url=http://localhost:8081 \
                                --topic customer_primitive_avro \
                                --from-beginning | \
                                jq '.'

load the connector
==================
/Users/e1xx/Downloads/confluent-5.4.1/bin/confluent local load jdbc-sink -- -d customer_primitive_avro-sink.json

Tail the connector log
======================
This path may change.
tail -f /var/folders/q7/xsq29v6s659dq4xl0ll36bg0wzq6w9/T/confluent.cnlBWQI6/connect/connect.stdout

./confluent-5.4.1/bin/confluent local log connect --f

To connect to psql
====================
psql -h localhost -U marvel nap-poc

To list all the connectors
==========================
curl http://localhost:8083/connectors

To delete all the connectors
============================
for i in `curl http://localhost:8083/connectors| jq '.[]'`; do eval curl -X DELETE http://localhost:8083/connectors/${i}; done

List all topics
===============
curl http://localhost:8082/topics | jq '.'

To delete list of topics
=========================
Create a varaible arr and then run the below line.
for i in $arr[@]; do  /usr/local/confluent-5.2.1/bin//kafka-topics --zookeeper 127.0.0.1:2181 --delete --topic $i; done