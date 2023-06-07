package nl.marisabel.backend.savings.repository;

import nl.marisabel.backend.savings.entity.MonthlySavingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySavingRepository extends JpaRepository<MonthlySavingEntity, Long> {
}
