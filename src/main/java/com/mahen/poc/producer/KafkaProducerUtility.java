package com.mahen.poc.producer;

import com.mahendran.poc.kafka.Customer;
import com.mahendran.poc.kafka.CustomerWithArray;
import com.mahendran.poc.kafka.CustomerWithCustomerIdentity;
import com.mahendran.poc.kafka.addresses;
import com.mahendran.poc.kafka.customerIdentity;
import com.mahendran.poc.kafka.key;
import com.nordstrom.events.sdk.MessageHeaders;
import com.nordstrom.nap.arguments.Arguments;
import com.nordstrom.nap.utils.AvroUtils;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Publishes an event to a local kafka topic.
 */
public class KafkaProducerUtility {

  private static final Arguments arguments = new Arguments();
  private static final String SASL_ACCESS_KEY = "SASL_ACCESS_KEY";
  private static final String SASL_SECRET = "SASL_SECRET";
  private static KafkaProducer producer;

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

    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, arguments.bootstrapServers);
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        KafkaAvroSerializer.class.getName());
    props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    props.setProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        arguments.schemaRegistryUrl);
    props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
    if (arguments.useSasl) {
      addSaslProperties(props);
    }
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

  private static Customer customer(String id) {
    return Customer.newBuilder()
        .setId("customer-" + id)
        .setName("customer-" + id)
        .setAge(1)
        .setSalary(10000)
        .setDept("cs")
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
      System.out.println(new AvroUtils().toBytes(customer(id)).length);
      ProducerRecord<key, Customer> recordTo = new ProducerRecord<>(topic, key(id),
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
      ProducerRecord<key, CustomerWithCustomerIdentity> recordTo = new ProducerRecord<>(topic,
          customerWithCustomerIdentity(id));
//      addMessageHeader(recordTo, "CustomerEvent");
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

//    setNonProd();
    setLocalEnv();
    producer = kafkaProducer();

    String topic = "customer_primitive_avro_new";
    produceEvent(topic);
    String arrayTopic = "customer_array_avro";
    produceCustomerWithArray(arrayTopic);
    String customerIdentityAddressAvro = "customer_identity_address_avro";
    KafkaProducerUtility.produceCustomerIdentityAndAddress(customerIdentityAddressAvro);
    String customerIdentityAddressAvroTfivek = "customer_identity_address_avro_tfivek";
    KafkaProducerUtility.produceCustomerIdentityAndAddress(customerIdentityAddressAvroTfivek);

    String customerIdentityAddressAvro1 = "customer_identity_address_avro_no_headers_partitioned";
    for (int i = 0; i < 25000; i++) {
      KafkaProducerUtility.produceCustomerIdentityAndAddress(customerIdentityAddressAvro1);
    }
  }

  private static void addSaslProperties(Properties props) {
    props.setProperty(AbstractKafkaAvroSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE,
        "SASL_INHERIT");
    props.setProperty(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    props.setProperty(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
    props.setProperty(
        SaslConfigs.SASL_JAAS_CONFIG,
        String.format(
            "org.apache.kafka.common.security.scram.ScramLoginModule "
                + " required username=\"%s\" password=\"%s\";",
            getSecrets().get(SASL_ACCESS_KEY), getSecrets().get(SASL_SECRET)));
  }

  /**
   * Add secrets.
   *
   * @return Map of access and secret key value
   */
  private static Map<String, String> getSecrets() {
    return Map.of(SASL_ACCESS_KEY, "", SASL_SECRET, "");
  }

  private static void setNonProd() {
    arguments.bootstrapServers = "brook.nonprod.us-west-2.aws.proton.nordstrom.com:9093";
    arguments.schemaRegistryUrl = "https://schema-registry.nonprod.us-west-2.aws.proton.nordstrom.com/";
    arguments.useSasl = true;
    arguments.groupId = UUID.randomUUID().toString();
  }
}