package ma.enset.ebankbackend.repositories;

import ma.enset.ebankbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    @Query("""
            SELECT CASE
                     WHEN TYPE(b) = CurrentAccount THEN 'CA'
                     WHEN TYPE(b) = SavingAccount THEN 'SA'
                     ELSE 'UNKNOWN'
                   END,
                   COALESCE(SUM(b.balance), 0)
            FROM BankAccount b
            GROUP BY TYPE(b)
            """)
    List<Object[]> sumBalanceGroupedByAccountType();
}
