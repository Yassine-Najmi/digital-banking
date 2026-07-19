package ma.enset.ebankbackend.dtos;

import java.util.List;
import java.util.Map;

public record DashboardStatsDTO(
        long totalCustomers,
        long totalAccounts,
        Map<String, Double> balanceByAccountType,
        Map<String, Long> operationsByType,
        List<DailyOperationsDTO> operationsLast7Days
) {
}
