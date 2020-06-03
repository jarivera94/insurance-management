package insurance.management.kafka.consumer;

import static insurance.management.common.TopicsNames.INSURANCE_TOPIC;

import insurance.management.dto.domain.Insurance;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaListener("insurance-consumer")
public class InsuranceConsumerKafka {

  @Topic(INSURANCE_TOPIC)
  public void receiveInsurance(
      @KafkaKey String key, Insurance insuranceSingle) {
    log.info("InsuranceConsumerKafka::receiveInsurance got insurance {} with key {}", insuranceSingle.getInsuranceName(), key);
  }
}
