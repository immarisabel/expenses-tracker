package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.error.ResourceNotFoundException;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.savings.SavingsModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class SavingsController {

 private final SavingsService savingsService;
 private final TransactionService transactionService;
 private final GoalService goalService;

 public SavingsController(SavingsService savingsService, TransactionService transactionService, GoalService goalService) {
  this.savingsService = savingsService;
  this.transactionService = transactionService;
  this.goalService = goalService;
 }


 @PostMapping("/savings/allocate-savings/{month}")
 public String allocateSavings(@PathVariable String month,
                               @RequestBody List<SavingsModel> savingsDTOs,
                               RedirectAttributes redirectAttributes) {

  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
  YearMonth yearMonth = YearMonth.parse(month, monthFormatter);

  for (SavingsModel dto : savingsDTOs) {
   GoalEntity goal = goalService.getGoalById(dto.getGoalId())
           .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + dto.getGoalId()));

   log.info("Last amount of goal: " + goal.getLastAmount());

   goal.setLastAmount(goal.getLastAmount() + dto.getAmount());
   if (goal.getLastAmount() >= goal.getMaxAmount()) {
    goal.setReached(true);
   }

   log.info("New goal amount: " + goal.getName() + " : " + goal.getLastAmount());
   log.info("Goal reached? " + goal.isReached());

   goalService.saveNewGoal(goal);

   List<SavingsEntity> existingSavings = savingsService.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
   if (existingSavings.isEmpty()) {
    SavingsEntity newSavings = new SavingsEntity();
    newSavings.setAmount(dto.getAmount());
    newSavings.setSavingsMonth(yearMonth.getMonth());
    newSavings.setSavingYear(Year.of(yearMonth.getYear()));
    newSavings.setGoal(goal);
    newSavings.setMonthYear(String.valueOf(yearMonth));
    savingsService.save(newSavings);
   } else {
    for (SavingsEntity savings : existingSavings) {
     savings.setAmount(savings.getAmount() + dto.getAmount());
     savingsService.save(savings);
    }
   }
  }

  log.info("Savings allocated successfully!");

  return "savings/goals";
 }





 @GetMapping("/savings/allocate-savings/{month}")
 public String allocateSavingsInGoals(@PathVariable String month, Model model) {

  log.info("Original YearMonth: " + month);

// DATE FORMATTING FOR PAGINATION AND SAVING
  if (month == null || month.isEmpty()) {
   YearMonth currentYearMonth = YearMonth.now();
   month = currentYearMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
  }
  // Original formatting
  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
  YearMonth yearMonth = YearMonth.parse(month, monthFormatter);
  log.info("Parsed YearMonth: " + yearMonth);

  // Formatting for header
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
  String formattedDate = yearMonth.format(formatter);
  log.info("Formatted Date: " + formattedDate);

  // Fetch the monthly totals for the given month
  LocalDate startOfMonth = yearMonth.atDay(1);
  LocalDate endOfMonth = yearMonth.atEndOfMonth();
  int year = yearMonth.getYear();

  double totalAllocated = 0;
  Map<Long, Double> goalAllocatedAmountMap = new HashMap<>();
  List<GoalEntity> goals = goalService.getAllGoals();

  // calculate totalAllocated and populate goalAllocatedAmountMap
  for (GoalEntity goal : goals) {
   List<SavingsEntity> existingSavings = savingsService.findByGoalAndMonthYear(goal, String.valueOf(yearMonth));
   double totalSavingsForGoal = existingSavings.stream().mapToDouble(SavingsEntity::getAmount).sum();
   totalAllocated += totalSavingsForGoal;
   goalAllocatedAmountMap.put(goal.getId(), totalSavingsForGoal);
  }

  // calculate amount to allocate
  double difference = transactionService.calculateRemainingFundsByMonth(startOfMonth, endOfMonth);
  log.info("REMAINING FUNDS FOR : " + formattedDate + " [ " + difference + " ] ");
  double totalToAllocate = Math.round(difference * 100.00) / 100.00;
  DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  String roundedTotal = decimalFormat.format(totalToAllocate);

  model.addAttribute("goals", goals);
  model.addAttribute("totalToAllocate", roundedTotal);
  model.addAttribute("totalAllocated", totalAllocated);
  model.addAttribute("monthToAllocate", formattedDate);
  model.addAttribute("goalAllocatedAmountMap", goalAllocatedAmountMap);


  return "savings/goals";
 }


 // edit goals - [✔] add amount and name, [ ] remove, [ ] edit amount and name


 // METHODS I NEED (or controllers or service WTEVER):
 // - automate amount to distruibute ✔
 // - save a new goal ✔
 // - save the amounts allocated to savings ✔
 // TODO - display chart of savings per month
 // - display amount saved TOTAL next to each goal ✔
 // PAGES I NEED:
 // - goals settings (make✔ TODO edit, TODO delete)
 // - goals overview (table with totals and descriptions, ✔ and TODO  if reached (add a star or checkmark))
 // - TODO goal chart (per ID) (graphic not bars - per month)
 // - TODO left over per month chart (graphic, not bars)
 // - savings allocation (sliders) ✔
 // - TODO add a text box in case sliders are annoying to tim
 // - TODO change color of saved if teh amount is lower or higher than the goal
}

