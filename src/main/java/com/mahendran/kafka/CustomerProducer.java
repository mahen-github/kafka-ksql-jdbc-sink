package com.mahendran.kafka;

import com.mahendran.event.Customer;
import com.mahendran.event.key;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

/**
 * Publishes an event to a local kafka topic.
 */
public class CustomerProducer<K, T extends SpecificRecordBase> {

  private static Customer customer(String id) {
    return Customer.newBuilder()
        .setCustomerId("customer-" + id)
        .setAge(99)
        .setCreationTime(Instant.now().toEpochMilli())
        .build();
  }

  private static key key(String id) {
    return key.newBuilder()
        .setKey("customer-key::" + id)
        .build();
  }

  public static void main(String[] args) {
    var kafkaProducerUtility = new CustomerProducer<>();
    kafkaProducerUtility.produceEvent("customer");
  }

  /**
   * Publishes a local event with KafkaProducer.
   */
  public void produceEvent(String topic) {
    try {
      var id = UUID.randomUUID().toString();

      KafkaProducer<K, T> producer = kafkaProducer();
      var record = new ProducerRecord<>(topic, (K) key(id), (T) customer(id));
      addMessageHeader(record, "CustomerCreated");
      producer.send(record);
      producer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private KafkaProducer<K, T> kafkaProducer() {
    Properties props = new Properties();

    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        Constants.LOCAL_KAFKA_BOOTSTRAP_SERVER);
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        KafkaAvroSerializer.class.getName());
    props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        KafkaAvroSerializer.class.getName());
    props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    props.setProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        Constants.LOCAL_SCHEMA_REGISTRY_URL);
    props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
    return new KafkaProducer<>(props);
  }

  /**
   * Creates a header for the event and sets sample values.
   */
  void addMessageHeader(ProducerRecord<K, T> recordTo, String eventType) {
    var headers = recordTo.headers();
    BiConsumer<String, String> addToHeader = (key, value) -> headers
        .add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
    addToHeader.accept(Headers.TYPE, eventType);
    addToHeader.accept(Headers.EVENT_ID, "TEST_ID");
    addToHeader.accept(Headers.APP_ID, "APPID");
    addToHeader.accept(Headers.EVENT_TIME, String.valueOf(Instant.now().toEpochMilli()));
  }
}