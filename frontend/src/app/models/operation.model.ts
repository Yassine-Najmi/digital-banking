export type OperationType = 'DEBIT' | 'CREDIT';

export interface AccountOperationDTO {
  id: number;
  operationDate: string;
  amount: number;
  type: OperationType;
  description: string;
}

export interface AccountHistoryDTO {
  accountId: string;
  balance: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDTOS: AccountOperationDTO[];
}
