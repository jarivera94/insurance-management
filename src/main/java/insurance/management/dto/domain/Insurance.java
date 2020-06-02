package insurance.management.dto.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Insurance {

  private String insuranceName;
  private String mnemonic;
  private String feeAmount;
  private List<String> coverages;
}
