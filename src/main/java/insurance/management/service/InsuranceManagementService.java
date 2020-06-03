package insurance.management.service;

import static insurance.management.common.MaterializedOutputNames.CUSTOMER_GLOBAL_TABLE;
import static insurance.management.common.MaterializedOutputNames.INSURANCES_GLOBAL_TABLE;

import insurance.management.dto.domain.Customer;
import insurance.management.dto.domain.Insurance;
import insurance.management.dto.domain.Sale;
import insurance.management.dto.service.inputRequest.CreateCustomerInput;
import insurance.management.dto.service.inputRequest.CreateInsuranceInput;
import insurance.management.dto.service.inputRequest.CreateSalesInput;
import insurance.management.kafka.client.CustomerKafkaClient;
import insurance.management.kafka.client.InsuranceKafkaClient;
import insurance.management.kafka.client.SalesKafkaClient;
import insurance.management.service.graphql.GraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.micronaut.validation.Validated;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@Slf4j
@Validated
@GraphQLService
public class InsuranceManagementService {

  private final InsuranceKafkaClient insuranceKafkaClient;
  private final CustomerKafkaClient customerKafkaClient;
  private final KafkaStreams kafkaStreams;
  private final SalesKafkaClient salesKafkaClient;

  public InsuranceManagementService(
      InsuranceKafkaClient insuranceKafkaClient,
      CustomerKafkaClient customerKafkaClient, KafkaStreams kafkaStreams,
      SalesKafkaClient salesKafkaClient) {
    this.insuranceKafkaClient = insuranceKafkaClient;
    this.customerKafkaClient = customerKafkaClient;
    this.kafkaStreams = kafkaStreams;
    this.salesKafkaClient = salesKafkaClient;
  }

  private static final Function<Customer, String> CREATE_KEY_CUSTOMER =
      customer -> customer.getDocumentType() + "::" + customer.getDocumentNumber();

  @GraphQLMutation(name = "generateInsurance")
  public Insurance generateInsurance(
      @GraphQLArgument(name = "insurance", description = " insurance object") @GraphQLNonNull @Valid
          CreateInsuranceInput createInsuranceInput) {
    Insurance insurance = new Insurance();
    insurance.setInsuranceName(createInsuranceInput.getInsuranceName());
    insurance.setMnemonic(createInsuranceInput.getMnemonic());
    insurance.setFeeAmount(createInsuranceInput.getFeeAmount());
    insurance.setCoverages(createInsuranceInput.getCoverages());

    insuranceKafkaClient
        .sendInsurance(createInsuranceInput.getInsuranceName(), insurance)
        .doOnSuccess(
            result ->
                log.info(
                    "InsuranceManagementService::generateInsurance insurance created successful {} ",
                    result.getInsuranceName()))
        .doOnError(
            error ->
                log.info(
                    "InsuranceManagementService::generateInsurance there is a error creating the insurance {} , see the below error {}",
                    insurance.getInsuranceName(),
                    error.getMessage()))
        .subscribe();

    return insurance;
  }

  @GraphQLMutation(name = "createCustomer")
  public Customer createCustomer(
      @GraphQLArgument(name = "customer", description = "customer object") @GraphQLNonNull @Valid
          CreateCustomerInput createCustomerInput) {
    Customer customer =
        Customer.builder()
            .documentNumber(createCustomerInput.getDocumentNumber())
            .documentType(createCustomerInput.getDocumentType())
            .fullName(createCustomerInput.getFullName())
            .age(createCustomerInput.getAge())
            .profession(createCustomerInput.getProfession())
            .build();

    customerKafkaClient
        .saveCustomer(CREATE_KEY_CUSTOMER.apply(customer), customer)
        .doOnSuccess(
            result ->
                log.info(
                    "InsuranceManagementService::createCustomer  user {} saved successful",
                    CREATE_KEY_CUSTOMER.apply(result)))
        .doOnError(
            error ->
                log.info(
                    "InsuranceManagementService::createCustomer there is a problem saving the user {} , please see the following track {}",
                    CREATE_KEY_CUSTOMER.apply(customer),
                    error.getMessage()))
        .subscribe();

    return customer;
  }

  @GraphQLMutation(name = "createSale")
  public Sale createSale(@GraphQLArgument(name ="sale")CreateSalesInput createSalesInput){

    Sale sale = Sale.builder()
        .customerId(createSalesInput.getCustomerId())
        .insuranceId(createSalesInput.getInsuranceId())
        .build();

    salesKafkaClient.sendSale(UUID.randomUUID().toString(), sale)
        .subscribe();

    return sale;
  }

  @GraphQLQuery(name = "retrieveInsurances")
  public List<Insurance> getAllInsurances() {

    final ReadOnlyKeyValueStore<String, Insurance> insurancesStore =
        kafkaStreams.store(
            INSURANCES_GLOBAL_TABLE, QueryableStoreTypes.<String, Insurance>keyValueStore());
    log.info(
        "InsuranceManagementService::getAllInsurances entries store {}",
        insurancesStore.approximateNumEntries());

    ArrayList<Insurance> insuranceList = new ArrayList<>();
    KeyValueIterator<String, Insurance> insurancesIterator = insurancesStore.all();
    while (insurancesIterator.hasNext()) {
      KeyValue<String, Insurance> kv = insurancesIterator.next();
      log.info("InsuranceManagementService::getAllInsurances iterator with key {}", kv.key);
      insuranceList.add(kv.value);
    }
    return insuranceList;
  }

  @GraphQLQuery(name = "retrieveCustomers")
  public List<Customer> getAllCustomers() {
    final ReadOnlyKeyValueStore<String, Customer> customerStore =
        kafkaStreams.store(
            CUSTOMER_GLOBAL_TABLE, QueryableStoreTypes.<String, Customer>keyValueStore());

    log.info(
        "InsuranceManagementService::getAllCustomers entries store {}",
        customerStore.approximateNumEntries());

    ArrayList<Customer> customerList = new ArrayList<>();
    KeyValueIterator<String, Customer> customerIterator = customerStore.all();
    while (customerIterator.hasNext()) {
      KeyValue<String, Customer> kv = customerIterator.next();
      log.info("InsuranceManagementService::getAllCustomers iterator with key {}", kv.key);
      customerList.add(kv.value);
    }
    return customerList;
  }
}
