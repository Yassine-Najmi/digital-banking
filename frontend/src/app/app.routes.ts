import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CustomersComponent } from './customers/customers.component';
import { NewCustomerComponent } from './customers/new-customer.component';
import { EditCustomerComponent } from './customers/edit-customer.component';
import { AccountsComponent } from './accounts/accounts.component';
import { ChatbotComponent } from './chatbot/chatbot.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'customers', component: CustomersComponent, canActivate: [authGuard] },
  { path: 'new-customer', component: NewCustomerComponent, canActivate: [authGuard] },
  { path: 'edit-customer/:id', component: EditCustomerComponent, canActivate: [authGuard] },
  { path: 'accounts', component: AccountsComponent, canActivate: [authGuard] },
  { path: 'chatbot', component: ChatbotComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];
