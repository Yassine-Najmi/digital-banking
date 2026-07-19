package ma.enset.ebankbackend.services;

import ma.enset.ebankbackend.dtos.DailyOperationsDTO;
import ma.enset.ebankbackend.dtos.DashboardStatsDTO;
import ma.enset.ebankbackend.enums.OperationType;
import ma.enset.ebankbackend.repositories.AccountOperationRepository;
import ma.enset.ebankbackend.repositories.BankAccountRepository;
import ma.enset.ebankbackend.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    public DashboardService(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }

    public DashboardStatsDTO getStats() {
        long totalCustomers = customerRepository.count();
        long totalAccounts = bankAccountRepository.count();

        Map<String, Double> balanceByAccountType = new LinkedHashMap<>();
        balanceByAccountType.put("CA", 0.0);
        balanceByAccountType.put("SA", 0.0);
        for (Object[] row : bankAccountRepository.sumBalanceGroupedByAccountType()) {
            String type = String.valueOf(row[0]);
            double sum = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
            balanceByAccountType.put(type, sum);
        }

        Map<String, Long> operationsByType = new LinkedHashMap<>();
        operationsByType.put(OperationType.DEBIT.name(), 0L);
        operationsByType.put(OperationType.CREDIT.name(), 0L);
        for (Object[] row : accountOperationRepository.countGroupedByOperationType()) {
            OperationType type = (OperationType) row[0];
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            operationsByType.put(type.name(), count);
        }

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(6);
        Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Map<LocalDate, Long> countsByDay = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            countsByDay.put(from.plusDays(i), 0L);
        }
        for (Object[] row : accountOperationRepository.countOperationsGroupedByDaySince(fromDate)) {
            LocalDate day = toLocalDate(row[0]);
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            if (countsByDay.containsKey(day)) {
                countsByDay.put(day, count);
            }
        }

        List<DailyOperationsDTO> operationsLast7Days = new ArrayList<>();
        countsByDay.forEach((day, count) ->
                operationsLast7Days.add(new DailyOperationsDTO(day.toString(), count))
        );

        return new DashboardStatsDTO(
                totalCustomers,
                totalAccounts,
                balanceByAccountType,
                operationsByType,
                operationsLast7Days
        );
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (value instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.parse(String.valueOf(value));
    }
}
