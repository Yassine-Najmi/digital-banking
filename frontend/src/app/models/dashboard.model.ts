export interface DailyOperationsDTO {
  date: string;
  count: number;
}

export interface DashboardStatsDTO {
  totalCustomers: number;
  totalAccounts: number;
  balanceByAccountType: Record<string, number>;
  operationsByType: Record<string, number>;
  operationsLast7Days: DailyOperationsDTO[];
}
