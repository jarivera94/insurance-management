package insurance.management.kafka.consumer;

import insurance.management.dto.domain.Insurance;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@KafkaListener("insurance-consumer")
public class InsuranceConsumerKafka {

  @Topic("insurances-topic")
  public void receiveInsurance(
      @KafkaKey String key, Insurance insuranceSingle) {
    log.info("Got {} with key {}", insuranceSingle.getInsuranceName(), key);
  }
}
