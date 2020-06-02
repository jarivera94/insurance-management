package insurance.management.dto.input;

import io.micronaut.validation.Validated;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CreateCustomerInput implements Serializable {

  private String documentNumber;
  private String documentType;
  private String fullName;
  private String age;
  private String profession;
}
