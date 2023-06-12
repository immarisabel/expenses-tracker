package nl.marisabel.backend.savings.repository;

import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsRepository extends JpaRepository<SavingsEntity, Long> {
    List<SavingsEntity> findByGoal(GoalEntity goal);

}

