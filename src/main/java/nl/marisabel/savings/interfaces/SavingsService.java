package nl.marisabel.savings.interfaces;

import nl.marisabel.savings.classes.GoalEntity;
import nl.marisabel.savings.classes.SavingsEntity;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SavingsService {
 public List<SavingsEntity> getAllSavings();

 public SavingsEntity save(SavingsEntity savingsEntity);

 public void updateSavings(GoalEntity goal, YearMonth yearMonth, double amount);

 public Map<String, Double> calculateMonthlySavings(List<SavingsEntity> savings);

 public List<SavingsEntity> findByGoalAndMonthYear(GoalEntity goal, String monthYear);
}
