import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

interface LoginResponse {
  'access-token': string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'access-token';

  username: string | null = null;
  roles: string[] = [];

  constructor(private http: HttpClient, private router: Router) {
    this.loadFromStorage();
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.backendHost}/auth/login`, { username, password })
      .pipe(
        tap((response) => {
          const token = response['access-token'];
          localStorage.setItem(this.tokenKey, token);
          this.decodeToken(token);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.username = null;
    this.roles = [];
    this.router.navigateByUrl('/login');
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    return this.roles.includes(role);
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  private loadFromStorage(): void {
    const token = this.getToken();
    if (token) {
      this.decodeToken(token);
    }
  }

  private decodeToken(token: string): void {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.username = payload.sub ?? null;
      const scope: string = payload.scope ?? '';
      this.roles = scope.split(' ').filter((role: string) => role.length > 0);
    } catch {
      this.username = null;
      this.roles = [];
    }
  }
}
