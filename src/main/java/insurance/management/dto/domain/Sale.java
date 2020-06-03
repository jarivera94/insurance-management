package insurance.management.dto.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Sale {

  private String insuranceId;
  private String customerId;
}
