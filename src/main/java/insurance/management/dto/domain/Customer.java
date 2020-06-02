package insurance.management.dto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class Customer {

  private String documentNumber;
  private String documentType;
  private String fullName;
  private String age;
  private String profession;
}
