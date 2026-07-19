import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CustomerDTO } from '../models/customer.model';
import { AuthService } from '../services/auth.service';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {
  customers: CustomerDTO[] = [];
  keyword = '';
  errorMessage = '';

  constructor(
    private customerService: CustomerService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.searchCustomers();
  }

  searchCustomers(): void {
    this.customerService.searchCustomers(this.keyword).subscribe({
      next: (data) => {
        this.customers = data;
        this.errorMessage = '';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de la recherche des clients';
      }
    });
  }

  deleteCustomer(customer: CustomerDTO): void {
    if (!customer.id) {
      return;
    }
    if (!confirm(`Supprimer le client ${customer.name} ?`)) {
      return;
    }
    this.customerService.deleteCustomer(customer.id).subscribe({
      next: () => this.searchCustomers(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Suppression impossible';
      }
    });
  }
}
