import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ChartConfiguration } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { DashboardStatsDTO } from '../models/dashboard.model';
import { DashboardService } from '../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [BaseChartDirective, DecimalPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  stats: DashboardStatsDTO | null = null;
  errorMessage = '';
  loading = true;

  doughnutChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Compte courant (CA)', 'Compte épargne (SA)'],
    datasets: [{ data: [0, 0], backgroundColor: ['#0d6efd', '#198754'] }]
  };
  doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
  };

  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['DEBIT', 'CREDIT'],
    datasets: [{ data: [0, 0], label: 'Opérations', backgroundColor: ['#dc3545', '#198754'] }]
  };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
    plugins: { legend: { display: false } }
  };

  lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Opérations / jour',
        borderColor: '#0d6efd',
        backgroundColor: 'rgba(13, 110, 253, 0.15)',
        fill: true,
        tension: 0.3
      }
    ]
  };
  lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
  };

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.doughnutChartData = {
          labels: ['Compte courant (CA)', 'Compte épargne (SA)'],
          datasets: [
            {
              data: [stats.balanceByAccountType['CA'] ?? 0, stats.balanceByAccountType['SA'] ?? 0],
              backgroundColor: ['#0d6efd', '#198754']
            }
          ]
        };
        this.barChartData = {
          labels: ['DEBIT', 'CREDIT'],
          datasets: [
            {
              data: [stats.operationsByType['DEBIT'] ?? 0, stats.operationsByType['CREDIT'] ?? 0],
              label: 'Opérations',
              backgroundColor: ['#dc3545', '#198754']
            }
          ]
        };
        this.lineChartData = {
          labels: stats.operationsLast7Days.map((d) => d.date),
          datasets: [
            {
              data: stats.operationsLast7Days.map((d) => d.count),
              label: 'Opérations / jour',
              borderColor: '#0d6efd',
              backgroundColor: 'rgba(13, 110, 253, 0.15)',
              fill: true,
              tension: 0.3
            }
          ]
        };
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Impossible de charger le tableau de bord';
        this.loading = false;
      }
    });
  }

  get totalBalance(): number {
    if (!this.stats) {
      return 0;
    }
    return (this.stats.balanceByAccountType['CA'] ?? 0) + (this.stats.balanceByAccountType['SA'] ?? 0);
  }
}
