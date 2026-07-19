package ma.enset.ebankbackend.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrentBankAccountDTO extends BankAccountDTO {
    private double overDraft;
}
