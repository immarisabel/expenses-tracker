package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SavingsService {
 public List<SavingsEntity> getAllSavings();
 public List<SavingsEntity> findByGoalAndMonthYear(GoalEntity goal, String monthYear);


 public SavingsEntity save(SavingsEntity savingsEntity);
 public void updateSavings(GoalEntity goal, YearMonth yearMonth, double amount);


 public Map<String, Double> calculateMonthlySavings(List<SavingsEntity> savings);
 public Map<Long, Double> calculateGoalAllocatedAmountMap(YearMonth yearMonth);

}
