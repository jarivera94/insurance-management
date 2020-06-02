package insurance.management.streams.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

  private static final String STREAM_ALL_INSURANCES = "insurance-all-stream";
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).findAndRegisterModules();

  @Singleton
  @Named(STREAM_ALL_INSURANCES)
  KafkaStreams viewInsurances(ConfiguredStreamBuilder builder) {

    Properties props = builder.getConfiguration();
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    Serde<Insurance> insuranceSerde = new Jackson2Serde<>(OBJECT_MAPPER, Insurance.class);

    final GlobalKTable<String, Insurance> table =
        builder.globalTable(
            "insurances-topic",
            Materialized.<String, Insurance, KeyValueStore<Bytes, byte[]>>as(
                    "insurances-global-table")
                .withKeySerde(Serdes.String())
                .withValueSerde(insuranceSerde));



    return new KafkaStreams(builder.build(), props);
  }
}
