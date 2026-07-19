package ma.enset.ebankbackend.repositories;

import ma.enset.ebankbackend.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {

    List<AccountOperation> findByBankAccountId(String accountId);

    Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);

    @Query("""
            SELECT o.type, COUNT(o)
            FROM AccountOperation o
            GROUP BY o.type
            """)
    List<Object[]> countGroupedByOperationType();

    @Query("""
            SELECT CAST(o.operationDate AS LocalDate), COUNT(o)
            FROM AccountOperation o
            WHERE o.operationDate >= :fromDate
            GROUP BY CAST(o.operationDate AS LocalDate)
            ORDER BY CAST(o.operationDate AS LocalDate)
            """)
    List<Object[]> countOperationsGroupedByDaySince(@Param("fromDate") Date fromDate);
}
