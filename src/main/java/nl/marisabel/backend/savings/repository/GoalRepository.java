package nl.marisabel.backend.savings.repository;

import nl.marisabel.backend.savings.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {
}
