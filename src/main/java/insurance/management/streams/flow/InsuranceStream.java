package insurance.management.streams.flow;

import static insurance.management.common.MaterializedOutputNames.CUSTOMER_GLOBAL_TABLE;
import static insurance.management.common.MaterializedOutputNames.INSURANCES_GLOBAL_TABLE;
import static insurance.management.common.StreamsNames.STREAM_ALL_INSURANCES;
import static insurance.management.common.TopicsNames.CUSTOMER_TOPIC;
import static insurance.management.common.TopicsNames.INSURANCE_TOPIC;
import static insurance.management.common.TopicsNames.SALES_TOPIC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import insurance.management.dto.domain.Customer;
import insurance.management.dto.domain.Insurance;
import insurance.management.dto.domain.Sale;
import insurance.management.dto.domain.SaleCustomer;
import insurance.management.dto.domain.SaleDetail;
import insurance.management.kafka.serde.Jackson2Serde;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
@Factory
public class InsuranceStream {

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).findAndRegisterModules();
  private static final String SALES_DETAIL_TOPIC ="sales-detail-topic" ;

  @Singleton
  @Named(STREAM_ALL_INSURANCES)
  KafkaStreams viewInsurances(ConfiguredStreamBuilder builder) {

    Properties props = builder.getConfiguration();

    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    Serde<Insurance> insuranceSerde = new Jackson2Serde<>(OBJECT_MAPPER, Insurance.class);
    Serde<Customer> customerSerde = new Jackson2Serde<>(OBJECT_MAPPER, Customer.class);
    Serde<Sale> saleSerde = new Jackson2Serde<>(OBJECT_MAPPER, Sale.class);
    Serde<SaleDetail> saleDetailSerde = new Jackson2Serde<>(OBJECT_MAPPER, SaleDetail.class);

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

    KStream<String, Sale> streamSales =
        builder.stream(SALES_TOPIC, Consumed.with(Serdes.String(), saleSerde));

    KStream<String, SaleCustomer> streamSalesCustomer =
        streamSales.join(
            globalTableCustomer,
            (saleId, sale) -> sale.getCustomerId(),
            (sales, customer) -> SaleCustomer.builder().sale(sales).customer(customer).build());

    KStream<String, SaleDetail> streamSalesCustomerDetail =
        streamSalesCustomer.join(
            globalTableInsurance,
            (salesId, saleCustomer) -> saleCustomer.getSale().getInsuranceId(),
            (salesOrder, insurance) ->
                SaleDetail.builder()
                    .insurance(insurance)
                    .customer(salesOrder.getCustomer())
                    .build());

    streamSalesCustomerDetail.to(SALES_DETAIL_TOPIC, Produced
        .with(Serdes.String(), saleDetailSerde));

    return new KafkaStreams(builder.build(), props);
  }
}
