package in.manikanta.moneymanager.repository;

import in.manikanta.moneymanager.entity.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
}
