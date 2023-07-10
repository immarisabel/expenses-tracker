package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.savings.entity.GoalEntity;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GoalService {
 public void saveOrUpdate(GoalEntity goal);

 public List<GoalEntity> getAllGoals();

 public Optional<GoalEntity> getGoalById(Long id);

 public void updateGoalState(GoalEntity goal, double amount);

}
