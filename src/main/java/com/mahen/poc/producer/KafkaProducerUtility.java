package com.mahen.poc.producer;

import com.mahendran.poc.kafka.CustomerWithArray;
import com.mahendran.poc.kafka.CustomerWithCustomerIdentity;
import com.mahendran.poc.kafka.addresses;
import com.mahendran.poc.kafka.customer;
import com.mahendran.poc.kafka.customerIdentity;
import com.mahendran.poc.kafka.key;
import com.nordstrom.events.sdk.MessageHeaders;
import com.nordstrom.nap.arguments.Arguments;
import com.nordstrom.nap.utils.AvroUtils;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

/**
 * Publishes an event to a local kafka topic.
 */
public class KafkaProducerUtility {

  private static final Arguments arguments = new Arguments();

  /**
   * Overwrites argument fields with local development values.
   */
  private static void setLocalEnv() {
    arguments.applicationId = "AppIdSample";
    arguments.groupId = UUID.randomUUID().toString();
    arguments.bootstrapServers = "localhost:9092";
    arguments.schemaRegistryUrl = "http://localhost:8081";
    arguments.useSasl = false;

  }

  private static KafkaProducer kafkaProducer() {
    Properties props = new Properties();
    props.setProperty("bootstrap.servers", arguments.bootstrapServers);
    props.setProperty("key.serializer", KafkaAvroSerializer.class.getName());
    props.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
    props.setProperty("compression.type", "snappy");
    props.setProperty("schema.registry.url", arguments.schemaRegistryUrl);
    props.put("auto.register.schemas", true);

    return new KafkaProducer(props);
  }

  /**
   * Creates a header for the event and sets sample values.
   */
  static void addMessageHeader(ProducerRecord recordTo, String eventType) {
    Long timestamp = Instant.now().toEpochMilli();
    var headers = recordTo.headers();

    BiConsumer<String, String> addToHeader = (key, value) -> headers
        .add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
    addToHeader.accept(MessageHeaders.TYPE_HEADER_KEY, eventType);
    addToHeader.accept(MessageHeaders.ID_HEADER_KEY, "TEST_ID");
    addToHeader.accept(MessageHeaders.APPID_HEADER_KEY, "APPID");
    addToHeader.accept(MessageHeaders.EVENTTIME_HEADER_KEY, timestamp.toString());
    addToHeader.accept(MessageHeaders.SYSTEMTIME_HEADER_KEY, timestamp.toString());
  }

  private static CustomerWithArray customerWithArray(String id) {
    return CustomerWithArray.newBuilder()
        .setCustomer("customer-" + id)
        .setAge(22)
        .setDept("CS")
        .setAttributedBy(List.of("WEB", "STORE"))
        .setCreationTime(Instant.now().toEpochMilli())
        .build();
  }

  private static CustomerWithCustomerIdentity customerWithCustomerIdentity(String id) {
    return CustomerWithCustomerIdentity.newBuilder()
        .setId("customer-" + id)
        .setAge(1)
        .setDept("CS")
        .setCustomerIdentity(
            customerIdentity.newBuilder()
                .setFirstName("mahendran")
                .setLastName("ponnusamy")
                .build())
        .setAddresses(List.of(
            addresses.newBuilder()
                .setAddressId("address-" + Instant.now())
                .build()
        ))
        .setAttributedBy(List.of("WEB", "STORE"))
        .setCreationTime(Instant.now().toEpochMilli())
        .build();
  }

  private static customer customer(String id) {
    return customer.newBuilder()
        .setId("customer-" + id)
        .setName("customer-" + id)
        .setAge(1)
        .setSalary(10000)
        .setCreationTime(Instant.now().toEpochMilli())
        .build();
  }

  private static key key(String id) {
    return key.newBuilder()
        .setKey("customer-key::" + id)
        .build();
  }

  /**
   * Publishes a local event with KafkaProducer.
   */
  public static void produceEvent(String topic) {
    try {
      var id = UUID.randomUUID().toString();
      System.out.println(id);
      setLocalEnv();

      KafkaProducer producer = kafkaProducer();
      ProducerRecord<key, customer> recordTo = new ProducerRecord<>(topic, key(id),
          customer(id));
      producer.send(recordTo);
      producer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Publishes a local event with KafkaProducer.
   */
  public static void produceCustomerWithArray(String topic) {
    try {
      var id = UUID.randomUUID().toString();
      setLocalEnv();
      KafkaProducer producer = kafkaProducer();
      ProducerRecord<key, CustomerWithArray> recordTo = new ProducerRecord<>(topic,
          key(id),
          customerWithArray(id));
      producer.send(recordTo);
      producer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Publishes a local event with KafkaProducer.
   */
  public static void produceCustomerIdentityAndAddress(String topic) {
    try {
      var id = UUID.randomUUID().toString();
      setLocalEnv();
      System.out.println(new AvroUtils().toBytes(customerWithCustomerIdentity(id)).length);
      KafkaProducer producer = kafkaProducer();
      ProducerRecord<key, CustomerWithCustomerIdentity> recordTo = new ProducerRecord<>(topic,
          key(id),
          customerWithCustomerIdentity(id));
      producer.send(recordTo);
      producer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Entry point for LocalEventUtility event producer.
   */
  public static void main(String[] args) {

    String topic = "customer_primitive_avro";
    produceEvent(topic);
    String arrayTopic = "customer_array_avro";
    produceCustomerWithArray(arrayTopic);
    String customerIdentityAddressAvro = "customer_identity_address_avro";
    KafkaProducerUtility.produceCustomerIdentityAndAddress(customerIdentityAddressAvro);
    String customerIdentityAddressAvroTfivek = "customer_identity_address_avro_tfivek";
    KafkaProducerUtility.produceCustomerIdentityAndAddress(customerIdentityAddressAvroTfivek);
  }
}