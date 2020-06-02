package insurance.management.kafka.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class Jackson2Deserializer<T> implements Deserializer<T> {

  private final ObjectMapper objectMapper;

  private Class<T> forType;


  public Jackson2Deserializer(ObjectMapper objectMapper, Class<T> forType) {
    this.objectMapper = objectMapper;
    this.forType = forType;
  }


  public Jackson2Deserializer(Class<T> forType) {
    this(new ObjectMapper(), forType);
  }


  @Override
  public T deserialize(String topic, byte[] bytes) {
    if (bytes == null) {
      return null;
    }

    try {
      return objectMapper.readValue(bytes, forType);
    } catch (Exception e) {
      String s = Base64.getEncoder().encodeToString(bytes);
      throw new SerializationException(e);
    }
  }


  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}


  @Override
  public void close() {}
}
