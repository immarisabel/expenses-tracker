package nl.marisabel.backend.savings.repository;

import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SavingsRepository extends JpaRepository<SavingsEntity, Long> {
    List<SavingsEntity> findByGoal(GoalEntity goal);

    @Query("SELECT s FROM SavingsEntity s WHERE s.goal = :goal AND s.monthYear = :monthYear")
    List<SavingsEntity> findByGoalAndMonthYear(GoalEntity goal, String monthYear);

}

