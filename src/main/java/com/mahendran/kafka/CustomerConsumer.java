package com.mahendran.kafka;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;

public class CustomerConsumer {

  private static Consumer<String, SpecificRecordBase> kafkaConsumer() {
    Properties props = new Properties();
    props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        Constants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
    props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        KafkaAvroDeserializer.class.getName());
    props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
    props.setProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        Constants.LOCAL_SCHEMA_REGISTRY_URL);

    return new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
  }

  private static void consume(String topicName) {
    var consumer = kafkaConsumer();
    consumer.subscribe(List.of(topicName));
    final int exitAfter = 10;

    try (consumer) {
      //noinspection InfiniteLoopStatement
      while (true) {
        ConsumerRecords<String, SpecificRecordBase> records = consumer
            .poll(Duration.ofMillis(100));
        if (records.count() > 0) {
          int counter = 0;
          for (ConsumerRecord<String, SpecificRecordBase> record : records) {
            System.out.println(record.value());
            if (++counter == exitAfter) {
              break;
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Main class.
   *
   * @param args program arguments
   */
  public static void main(String[] args) {
    final String topicName = "customer";
    consume(topicName);
  }
}
