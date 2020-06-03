package insurance.management.dto.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleDetail {

  private String key;
  private Customer customer;
  private Insurance insurance;
}
