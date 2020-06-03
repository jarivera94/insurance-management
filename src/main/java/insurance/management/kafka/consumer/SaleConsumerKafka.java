package insurance.management.kafka.consumer;

import static insurance.management.common.TopicsNames.SALES_TOPIC;

import insurance.management.dto.domain.Sale;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.Body;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaListener(groupId = "sale-consumer", pollTimeout = "500ms")
public class SaleConsumerKafka {

  @Topic(SALES_TOPIC)
  public void receiveSale(@KafkaKey String key, @Body Sale sale) {
    log.info("SaleConsumerKafka::receiveSale got sale key : {}, value : {}", key, sale);
  }
}
