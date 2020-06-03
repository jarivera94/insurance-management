package insurance.management.kafka.client;

import static insurance.management.common.TopicsNames.SALES_TOPIC;

import insurance.management.dto.domain.Sale;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.Body;
import io.reactivex.Single;

@KafkaClient("sales-producer")
public interface SalesKafkaClient {

  @Topic(SALES_TOPIC)
  Single<Sale> sendSale(@KafkaKey String key, @Body Sale body);
}
