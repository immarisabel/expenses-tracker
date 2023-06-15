package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.repository.SavingsRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SavingsService {
 private final SavingsRepository savingsRepository;
 private final GoalService goalService;

 public SavingsService(SavingsRepository savingsRepository, GoalService goalService) {
  this.savingsRepository = savingsRepository;
  this.goalService = goalService;
 }

 public SavingsEntity save(SavingsEntity savingsEntity) {
  return savingsRepository.save(savingsEntity);
 }

 public List<SavingsEntity> getAllSavings() {
  return savingsRepository.findAll();
 }


 public List<SavingsEntity> findByGoalAndMonthYear(GoalEntity goal, String monthYear) {
  return savingsRepository.findByGoalAndMonthYear(goal, monthYear);
 }

 public double calculateTotalAllocated(YearMonth yearMonth) {
  List<GoalEntity> goals = goalService.getAllGoals();
  double totalAllocated = 0;

  for (GoalEntity goal : goals) {
   List<SavingsEntity> existingSavings = this.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
   totalAllocated += existingSavings.stream().mapToDouble(SavingsEntity::getAmount).sum();
  }

  return totalAllocated;
 }

 public Map<Long, Double> calculateGoalAllocatedAmountMap(YearMonth yearMonth) {
  List<GoalEntity> goals = goalService.getAllGoals();
  Map<Long, Double> goalAllocatedAmountMap = new HashMap<>();

  for (GoalEntity goal : goals) {
   List<SavingsEntity> existingSavings = this.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
  }
  return goalAllocatedAmountMap;
 }

 public void updateSavings(GoalEntity goal, YearMonth yearMonth, double amount) {
  List<SavingsEntity> existingSavings = this.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
  if (existingSavings.isEmpty()) {
   SavingsEntity newSavings = new SavingsEntity();
   newSavings.setAmount(amount);
   newSavings.setSavingsMonth(yearMonth.getMonth());
   newSavings.setSavingYear(Year.of(yearMonth.getYear()));
   newSavings.setGoal(goal);
   newSavings.setMonthYear(String.valueOf(yearMonth));
   this.save(newSavings);
  } else {
   for (SavingsEntity savings : existingSavings) {
    savings.setAmount(savings.getAmount() + amount);
    this.save(savings);
   }
  }
 }

}