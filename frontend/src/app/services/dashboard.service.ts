import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { DashboardStatsDTO } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  getStats(): Observable<DashboardStatsDTO> {
    return this.http.get<DashboardStatsDTO>(`${environment.backendHost}/dashboard/stats`);
  }
}
