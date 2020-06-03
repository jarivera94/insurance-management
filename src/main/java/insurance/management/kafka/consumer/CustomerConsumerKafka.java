package insurance.management.kafka.consumer;

import static insurance.management.common.TopicsNames.CUSTOMER_TOPIC;

import insurance.management.dto.domain.Customer;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaListener("customer-consumer")
public class CustomerConsumerKafka {

  @Topic(CUSTOMER_TOPIC)
  public void receiveCustomer(@KafkaKey String key, Customer customer) {
    log.info("CustomerConsumerKafka::receiveCustomer got customer  {} with key {}", customer.getFullName(), key);
  }
}
