package insurance.management.streams.flow;

import static insurance.management.common.MaterializedOutputNames.CUSTOMER_GLOBAL_TABLE;
import static insurance.management.common.MaterializedOutputNames.INSURANCES_GLOBAL_TABLE;
import static insurance.management.common.StreamsNames.STREAM_ALL_INSURANCES;
import static insurance.management.common.TopicsNames.CUSTOMER_TOPIC;
import static insurance.management.common.TopicsNames.INSURANCE_TOPIC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import insurance.management.dto.domain.Customer;
import insurance.management.dto.domain.Insurance;
import insurance.management.kafka.serde.Jackson2Serde;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

@Factory
public class InsuranceStream {

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).findAndRegisterModules();

  @Singleton
  @Named(STREAM_ALL_INSURANCES)
  KafkaStreams viewInsurances(ConfiguredStreamBuilder builder) {

    Properties props = builder.getConfiguration();

    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    Serde<Insurance> insuranceSerde = new Jackson2Serde<>(OBJECT_MAPPER, Insurance.class);
    Serde<Customer> customerSerde = new Jackson2Serde<>(OBJECT_MAPPER, Customer.class);

    final GlobalKTable<String, Insurance> globalTableInsurance =
        builder.globalTable(
            INSURANCE_TOPIC,
            Materialized.<String, Insurance, KeyValueStore<Bytes, byte[]>>as(
                    INSURANCES_GLOBAL_TABLE)
                .withKeySerde(Serdes.String())
                .withValueSerde(insuranceSerde));

    final GlobalKTable<String, Customer> globalTableCustomer =
        builder.globalTable(
            CUSTOMER_TOPIC,
            Materialized.<String, Customer, KeyValueStore<Bytes, byte[]>>as(CUSTOMER_GLOBAL_TABLE)
                .withKeySerde(Serdes.String())
                .withValueSerde(customerSerde));

    return new KafkaStreams(builder.build(), props);
  }

}
