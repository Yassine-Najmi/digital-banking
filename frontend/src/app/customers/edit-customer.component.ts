import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-edit-customer',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-customer.component.html',
  styleUrl: './edit-customer.component.css'
})
export class EditCustomerComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly customerService = inject(CustomerService);

  customerId!: number;
  errorMessage = '';

  form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]]
  });

  ngOnInit(): void {
    this.customerId = Number(this.route.snapshot.paramMap.get('id'));
    this.customerService.getCustomer(this.customerId).subscribe({
      next: (customer) => this.form.patchValue({ name: customer.name, email: customer.email }),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Client introuvable';
      }
    });
  }

  handleUpdate(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.customerService
      .updateCustomer(this.customerId, { id: this.customerId, ...this.form.getRawValue() })
      .subscribe({
        next: () => this.router.navigateByUrl('/customers'),
        error: (err) => {
          this.errorMessage = err.error?.message || 'Mise à jour impossible';
        }
      });
  }
}
