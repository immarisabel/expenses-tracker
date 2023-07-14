package nl.marisabel.savings.interfaces;

import nl.marisabel.savings.classes.GoalEntity;
import nl.marisabel.savings.classes.SavingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SavingsRepository extends JpaRepository<SavingsEntity, Long> {
    List<SavingsEntity> findByGoal(GoalEntity goal);

    @Query("SELECT s FROM SavingsEntity s WHERE s.goal = :goal AND s.monthYear = :monthYear")
    List<SavingsEntity> findByGoalAndMonthYear(GoalEntity goal, String monthYear);

}

