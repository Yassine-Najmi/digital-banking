import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  BankAccountDTO,
  CreditDTO,
  DebitDTO,
  TransferRequestDTO
} from '../models/account.model';
import { AccountHistoryDTO, AccountOperationDTO } from '../models/operation.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly baseUrl = `${environment.backendHost}/accounts`;

  constructor(private http: HttpClient) {}

  getAccount(accountId: string): Observable<BankAccountDTO> {
    return this.http.get<BankAccountDTO>(`${this.baseUrl}/${accountId}`);
  }

  listAccounts(): Observable<BankAccountDTO[]> {
    return this.http.get<BankAccountDTO[]>(this.baseUrl);
  }

  getOperations(accountId: string): Observable<AccountOperationDTO[]> {
    return this.http.get<AccountOperationDTO[]>(`${this.baseUrl}/${accountId}/operations`);
  }

  getAccountHistory(accountId: string, page = 0, size = 5): Observable<AccountHistoryDTO> {
    return this.http.get<AccountHistoryDTO>(`${this.baseUrl}/${accountId}/pageOperations`, {
      params: { page, size }
    });
  }

  debit(payload: DebitDTO): Observable<DebitDTO> {
    return this.http.post<DebitDTO>(`${this.baseUrl}/debit`, payload);
  }

  credit(payload: CreditDTO): Observable<CreditDTO> {
    return this.http.post<CreditDTO>(`${this.baseUrl}/credit`, payload);
  }

  transfer(payload: TransferRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/transfer`, payload);
  }
}
