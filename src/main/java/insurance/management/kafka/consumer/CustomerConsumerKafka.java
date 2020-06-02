package insurance.management.kafka.consumer;

import insurance.management.dto.domain.Customer;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaListener("customer-consumer")
public class CustomerConsumerKafka {

  @Topic("customer-management")
  public void receiveCustomer(@KafkaKey String key, Customer customer) {
    log.info("Got from number 1 {} with key {}", customer.getFullName(), key);
  }
}
