import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './login/login.component';
import { CustomersComponent } from './customers/customers.component';
import { NewCustomerComponent } from './customers/new-customer.component';
import { EditCustomerComponent } from './customers/edit-customer.component';
import { AccountsComponent } from './accounts/accounts.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'customers', component: CustomersComponent, canActivate: [authGuard] },
  { path: 'new-customer', component: NewCustomerComponent, canActivate: [authGuard] },
  { path: 'edit-customer/:id', component: EditCustomerComponent, canActivate: [authGuard] },
  { path: 'accounts', component: AccountsComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/customers', pathMatch: 'full' },
  { path: '**', redirectTo: '/customers' }
];
