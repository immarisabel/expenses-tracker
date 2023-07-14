package nl.marisabel.savings.interfaces;

import nl.marisabel.savings.classes.GoalEntity;

import java.util.List;
import java.util.Optional;

public interface GoalService {
 public void saveOrUpdate(GoalEntity goal);

 public List<GoalEntity> getAllGoals();

 public Optional<GoalEntity> getGoalById(Long id);

 public void updateGoalState(GoalEntity goal, double amount);

}
