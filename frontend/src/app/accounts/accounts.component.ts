import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { BankAccountDTO } from '../models/account.model';
import { AccountHistoryDTO } from '../models/operation.model';
import { AccountService } from '../services/account.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, DatePipe, DecimalPipe],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css'
})
export class AccountsComponent {
  private readonly fb = inject(FormBuilder);
  private readonly accountService = inject(AccountService);
  readonly authService = inject(AuthService);

  accountId = '';
  account: BankAccountDTO | null = null;
  history: AccountHistoryDTO | null = null;
  currentPage = 0;
  pageSize = 5;
  pages: number[] = [];
  errorMessage = '';
  successMessage = '';

  operationForm = this.fb.nonNullable.group({
    operationType: ['CREDIT' as 'CREDIT' | 'DEBIT' | 'TRANSFER', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: [''],
    accountDestination: ['']
  });

  searchAccount(): void {
    if (!this.accountId.trim()) {
      this.errorMessage = 'Veuillez saisir un identifiant de compte';
      return;
    }
    this.currentPage = 0;
    this.loadAccount();
  }

  loadAccount(): void {
    this.errorMessage = '';
    this.accountService.getAccount(this.accountId.trim()).subscribe({
      next: (account) => {
        this.account = account;
        this.loadHistory();
      },
      error: (err) => {
        this.account = null;
        this.history = null;
        this.errorMessage = err.error?.message || 'Compte introuvable';
      }
    });
  }

  loadHistory(): void {
    this.accountService
      .getAccountHistory(this.accountId.trim(), this.currentPage, this.pageSize)
      .subscribe({
        next: (history) => {
          this.history = history;
          this.pages = Array.from({ length: history.totalPages }, (_, i) => i);
          if (this.account) {
            this.account.balance = history.balance;
          }
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Impossible de charger les opérations';
        }
      });
  }

  goToPage(page: number): void {
    if (!this.history || page < 0 || page >= this.history.totalPages) {
      return;
    }
    this.currentPage = page;
    this.loadHistory();
  }

  handleOperation(): void {
    if (!this.account || this.operationForm.invalid) {
      this.operationForm.markAllAsTouched();
      return;
    }

    const value = this.operationForm.getRawValue();
    const amount = Number(value.amount);
    const description = value.description || value.operationType;
    this.successMessage = '';
    this.errorMessage = '';

    const refresh = () => {
      this.successMessage = 'Opération effectuée';
      this.operationForm.patchValue({ amount: 0, description: '', accountDestination: '' });
      this.currentPage = 0;
      this.loadAccount();
    };

    const onError = (err: { error?: { message?: string } }) => {
      this.errorMessage = err.error?.message || 'Opération impossible';
    };

    switch (value.operationType) {
      case 'CREDIT':
        this.accountService
          .credit({ accountId: this.account.id, amount, description })
          .subscribe({ next: refresh, error: onError });
        break;
      case 'DEBIT':
        this.accountService
          .debit({ accountId: this.account.id, amount, description })
          .subscribe({ next: refresh, error: onError });
        break;
      case 'TRANSFER':
        if (!value.accountDestination.trim()) {
          this.errorMessage = 'Compte destination obligatoire pour un virement';
          return;
        }
        this.accountService
          .transfer({
            accountSource: this.account.id,
            accountDestination: value.accountDestination.trim(),
            amount,
            description
          })
          .subscribe({ next: refresh, error: onError });
        break;
      default: {
        const _exhaustive: never = value.operationType;
        return _exhaustive;
      }
    }
  }
}
