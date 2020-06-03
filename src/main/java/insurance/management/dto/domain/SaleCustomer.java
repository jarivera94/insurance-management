package insurance.management.dto.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleCustomer {

  private Sale sale;
  private Customer customer;
}
