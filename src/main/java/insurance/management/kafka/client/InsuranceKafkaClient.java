package insurance.management.kafka.client;

import insurance.management.dto.domain.Insurance;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.Body;
import io.reactivex.Single;

@KafkaClient
public interface InsuranceKafkaClient {

  @Topic("insurances-topic")
  Single<Insurance>sendInsurance(@KafkaKey String key , @Body Insurance body);
}
