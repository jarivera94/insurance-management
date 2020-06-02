package insurance.management.kafka.client;

import static insurance.management.common.TopicsNames.CUSTOMER_TOPIC;

import insurance.management.dto.domain.Customer;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.Body;
import io.reactivex.Single;

@KafkaClient("customer-producer")
public interface CustomerKafkaClient {

  @Topic(CUSTOMER_TOPIC)
  Single<Customer> saveCustomer(@KafkaKey String key, @Body Customer customer);
}
