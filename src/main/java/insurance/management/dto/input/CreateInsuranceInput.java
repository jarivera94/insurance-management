package insurance.management.dto.input;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateInsuranceInput {

  private String insuranceName;
  private String mnemonic;
  private String feeAmount;
  private List<String> coverages;
}
