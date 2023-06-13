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
import java.util.List;

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
   log.info("Goal reached? "+  goal.isReached());

   goalService.saveNewGoal(goal);

   SavingsEntity savingsEntity = new SavingsEntity();
   savingsEntity.setAmount(dto.getAmount());
   savingsEntity.setSavingsMonth(yearMonth.getMonth());
   savingsEntity.setSavingYear(Year.of(yearMonth.getYear()));
   savingsEntity.setGoal(goal);
   savingsEntity.setMonthYear(String.valueOf(yearMonth));

   savingsService.save(savingsEntity);
  }

  log.info("Savings allocated successfully!");

  return "savings/goals";
 }

//TODO don't allow new savings for the month, but load the current data if we need to edit
// - load where in the sliders it is
// - updated amounts

 /* PROMPT
 I have this controller to add savings to multiple goals.

 @PostMapping("/savings/allocate-savings/{month}")
 public String allocateSavings(@PathVariable String month,
                               @RequestBody List<SavingsModel> savingsDTOs,
                               RedirectAttributes redirectAttributes) {

  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
  YearMonth yearMonth = YearMonth.parse(month, monthFormatter);

  for (SavingsModel dto : savingsDTOs) {
   GoalEntity goal = goalService.getGoalById(dto.getGoalId())
           .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + dto.getGoalId()));
   log.info(goal.getSavingsEntities().size());
   log.info(goal.getId());
   SavingsEntity savingsEntity = new SavingsEntity();
   savingsEntity.setAmount(dto.getAmount());
   savingsEntity.setSavingsMonth(yearMonth.getMonth());
   savingsEntity.setSavingYear(Year.of(yearMonth.getYear()));
   savingsEntity.setGoal(goal);
   savingsEntity.setMonthYear(String.valueOf(yearMonth));

   savingsService.save(savingsEntity);
  }

  log.info("Savings allocated successfully!");

  return "savings/goals";
 }

 I need the goal entity to update the field 'lastAmount' to the sum of the previous 'lastAmount' + the added one from the month.
 Each goal will update this amount everytime savings are added.
 Once the amount matches the 'maxAmount', it should update automatically the boolean 'reached' to TRUE.
 So far all the savings and new goals process. Now I need to link them like that.
 Provide me the code for teh controller for all of this to happen automatically after submitting the savings.
 */

// example to use: http://localhost:9191/savings/allocate-savings/032017



 // THIS WORKS!
// shows the savings allocation page with sliders

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

  // Calculate the previous and next months
  YearMonth previousMonth = yearMonth.minusMonths(1);
  YearMonth nextMonth = yearMonth.plusMonths(1);

  // Fetch the monthly totals for the given month
  LocalDate startOfMonth = yearMonth.atDay(1);
  LocalDate endOfMonth = yearMonth.atEndOfMonth();
  int year = yearMonth.getYear();


  //calculate amount to allocate
  double difference = transactionService.calculateRemainingFundsByMonth(startOfMonth, endOfMonth);
  log.info("REMAINING FUNDS FOR : " + formattedDate + " [ " + difference + " ] ");
  double totalToAllocate = Math.round(difference * 100.00) / 100.00;
  DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  String roundedTotal = decimalFormat.format(totalToAllocate);

  double totalAllocated = 0;

  // DUMMY DATA
  List<GoalEntity> goals = goalService.getAllGoals();

  model.addAttribute("goals", goals);
  model.addAttribute("totalToAllocate", roundedTotal);
  model.addAttribute("totalAllocated", totalAllocated);
  model.addAttribute("monthToAllocate", formattedDate);

  return "savings/goals";
 }


 // edit goals - [✔] add amount and name, [ ] remove, [ ] edit amount and name


 // METHODS I NEED (or controllers or service WTEVER):
 // - automate amount to distruibute ✔
 // - save a new goal ✔
 // - save the amounts allocated to savings 🅿
 // - display chart of savings per month
 // - display amount saved TOTAL next to each goal
 // PAGES I NEED:
 // - goals settings (make, edit, delete)
 // - goals overview (table with totals and descriptions, and if reached)
 // - goal chart (per ID) (graphic not bars - per month)
 // - left over per month chart (graphic, not bars)
 // - savings allocation (sliders, in progress)
}

