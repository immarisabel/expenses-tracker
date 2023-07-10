package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalsAndSavingsService {

 private final GoalServiceImp goalService;
 private final SavingsServiceImp savingsService;

 public GoalsAndSavingsService(GoalServiceImp goalService, SavingsServiceImp savingsService) {
  this.goalService = goalService;
  this.savingsService = savingsService;
 }

 public GoalServiceImp getGoalService() {
  return goalService;
 }

 public SavingsServiceImp getSavingsService() {
  return savingsService;
 }

 /**
  * Calculates the total allocated amount for the specified year and month.
  *
  * @param yearMonth The YearMonth object representing the year and month for which to calculate the total allocated amount.
  * @return The total allocated amount for the specified year and month.
  */
 public double calculateTotalAllocated(YearMonth yearMonth) {
  List<GoalEntity> goals = goalService.getAllGoals();
  double totalAllocated = 0;

  for (GoalEntity goal : goals) {
   List<SavingsEntity> existingSavings = savingsService.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
   totalAllocated += existingSavings.stream().mapToDouble(SavingsEntity::getAmount).sum();
  }

  return totalAllocated;
 }

 /**
  * Calculates the goal allocated amount map for the specified year and month.
  *
  * @param yearMonth The YearMonth object representing the year and month for which to calculate the goal allocated amount map.
  * @return A Map where the key is the goal ID and the value is the allocated amount for that goal.
  */
 public Map<Long, Double> calculateGoalAllocatedAmountMap(YearMonth yearMonth) {
  List<GoalEntity> goals = goalService.getAllGoals();
  Map<Long, Double> goalAllocatedAmountMap = new HashMap<>();

  for (GoalEntity goal : goals) {
   List<SavingsEntity> existingSavings = savingsService.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
   // Additional processing can be done here
  }

  return goalAllocatedAmountMap;
 }

}
