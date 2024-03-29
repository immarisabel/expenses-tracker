package nl.marisabel.savings.classes;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.savings.interfaces.SavingsRepository;
import nl.marisabel.savings.interfaces.SavingsService;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <h2>SAVINGS SERVICE</h2>
 * This class provides methods to handle savings-related operations, such as saving savings entities,
 * retrieving all savings, calculating monthly savings, finding savings by goal and month-year,
 * calculating the total allocated amount, calculating the goal allocated amount map, and updating savings.
 */
@Log4j2
@Service
public class SavingsServiceImp implements SavingsService {

 private final SavingsRepository savingsRepository;

 public SavingsServiceImp(SavingsRepository savingsRepository) {
  this.savingsRepository = savingsRepository;
 }


 /**
  * Saves the provided savings entity.
  *
  * @param savingsEntity The SavingsEntity object to be saved.
  * @return The saved SavingsEntity object.
  */
 public SavingsEntity save(SavingsEntity savingsEntity) {
  return savingsRepository.save(savingsEntity);
 }

 /**
  * Retrieves all savings entities.
  *
  * @return A List of SavingsEntity objects representing all savings.
  */
 public List<SavingsEntity> getAllSavings() {
  return savingsRepository.findAll();
 }

 /**
  * Calculates the monthly savings based on the provided list of savings entities.
  *
  * @param savings The list of SavingsEntity objects for which to calculate monthly savings.
  * @return A Map of month-year and total savings amount for each month.
  *         The format of the map entry is "Month / Savings 0.00".
  */
 public Map<String, Double> calculateMonthlySavings(List<SavingsEntity> savings) {
  Map<String, Double> monthlySavings = new LinkedHashMap<>();

  for (SavingsEntity saving : savings) {
   String monthYear = saving.getMonthYear();
   double amount = saving.getAmount();

   monthYear = monthYear != null ? monthYear : "Unknown Month";

   monthlySavings.merge(monthYear, amount, Double::sum);
  }

  return monthlySavings;
 }


 /**
  * Finds savings entities by the given goal and month-year.
  *
  * @param goal      The GoalEntity object representing the goal.
  * @param monthYear The month-year string to match against the savings entities.
  * @return A List of SavingsEntity objects matching the given goal and month-year.
  */
 public List<SavingsEntity> findByGoalAndMonthYear(GoalEntity goal, String monthYear) {
  return savingsRepository.findByGoalAndMonthYear(goal, monthYear);
 }


 /**
  * Updates the savings for the given goal, year and month with the provided amount.
  *
  * @param goal      The GoalEntity object for which to update the savings.
  * @param yearMonth The YearMonth object representing the year and month for which to update the savings.
  * @param amount    The amount to add to the savings.
  */
 public void updateSavings(GoalEntity goal, YearMonth yearMonth, double amount) {
  List<SavingsEntity> existingSavings = this.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));

  if (existingSavings.isEmpty()) {
   SavingsEntity newSavings = new SavingsEntity();
   newSavings.setAmount(amount);
   newSavings.setSavingsMonth(yearMonth.getMonth());
   newSavings.setSavingYear(Year.of(yearMonth.getYear()));
   newSavings.setGoal(goal);
   newSavings.setMonthYear(String.valueOf(yearMonth));
   goal.setLastAmount(amount);
   this.save(newSavings);
  } else {
   for (SavingsEntity savings : existingSavings) {
    savings.setAmount(savings.getAmount() + amount);
    goal.setLastAmount(savings.getAmount() + amount);
    this.save(savings);
   }
  }
 }


 /**
  * Saves the provided savings amount for a specific goal and year-month.
  *
  * @param amount    The amount to be saved.
  * @param yearMonth The YearMonth object representing the year and month.
  * @param goal      The GoalEntity object for which to save the savings amount.
  */
 public void saveSavingsForGoal(double amount, YearMonth yearMonth, GoalEntity goal) {

  SavingsEntity savingsEntity = new SavingsEntity();
  savingsEntity.setAmount(amount);
  savingsEntity.setSavingsMonth(yearMonth.getMonth());
  savingsEntity.setSavingYear(Year.of(yearMonth.getYear()));
  savingsEntity.setGoal(goal);
  savingsEntity.setMonthYear(String.valueOf(yearMonth));

  savingsRepository.save(savingsEntity);
 }
}
