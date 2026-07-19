import { CustomerDTO } from './customer.model';

export type AccountStatus = 'CREATED' | 'ACTIVATED' | 'SUSPENDED';

export interface BankAccountDTO {
  id: string;
  balance: number;
  createdAt: string;
  status: AccountStatus;
  customerDTO: CustomerDTO;
  type: string;
}

export interface CurrentBankAccountDTO extends BankAccountDTO {
  overDraft: number;
}

export interface SavingBankAccountDTO extends BankAccountDTO {
  interestRate: number;
}

export interface DebitDTO {
  accountId: string;
  amount: number;
  description: string;
}

export interface CreditDTO {
  accountId: string;
  amount: number;
  description: string;
}

export interface TransferRequestDTO {
  accountSource: string;
  accountDestination: string;
  amount: number;
  description?: string;
}
