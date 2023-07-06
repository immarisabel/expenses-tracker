package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.error.ResourceNotFoundException;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalServiceImp;
import nl.marisabel.backend.savings.service.SavingsServiceImp;
import nl.marisabel.backend.transactions.service.TransactionServiceImp;
import nl.marisabel.backend.savings.model.SavingsModel;
import nl.marisabel.util.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/savings")
public class SavingsController {

 private final SavingsServiceImp savingsServiceImp;
 private final TransactionServiceImp transactionServiceImp;
 private final GoalServiceImp goalServiceImp;

 public SavingsController(SavingsServiceImp savingsServiceImp, TransactionServiceImp transactionServiceImp, GoalServiceImp goalServiceImp) {
  this.savingsServiceImp = savingsServiceImp;
  this.transactionServiceImp = transactionServiceImp;
  this.goalServiceImp = goalServiceImp;
 }


 @PostMapping("/allocate-savings/{month}")
 public String allocateSavings(@PathVariable String month,
                               @RequestBody List<SavingsModel> savingsDTOs,
                               RedirectAttributes redirectAttributes) {
  log.info("POST method allocateSavings() = Saving.... ");
  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
  YearMonth yearMonth = YearMonth.parse(month, monthFormatter);
  for (SavingsModel dto : savingsDTOs) {
   GoalEntity goal = goalServiceImp.getGoalByIdAndHandleException(dto.getGoalId());
   goalServiceImp.updateGoalState(goal, dto.getAmount());
   savingsServiceImp.saveSavingsForGoal(dto.getAmount(), yearMonth, goal);
  }
  log.info("Savings allocated successfully!");
  return "savings/allocate-savings";
 }


 @GetMapping("/allocate-savings/{month}")
 public String allocateSavingsInGoals(@PathVariable String month, Model model) {

  log.info("Original YearMonth: " + month);

  YearMonth yearMonth = DateUtils.parseToYearMonth(month);
  log.info("Parsed YearMonth: " + yearMonth);

  String formattedDate = DateUtils.formatForHeader(yearMonth);
  log.info("Formatted Date: " + formattedDate);

  YearMonth previousMonth = yearMonth.minusMonths(1);
  YearMonth nextMonth = yearMonth.plusMonths(1);

  LocalDate startOfMonth = yearMonth.atDay(1);
  LocalDate endOfMonth = yearMonth.atEndOfMonth();

  double totalAllocated = savingsServiceImp.calculateTotalAllocated(yearMonth);
  Map<Long, Double> goalAllocatedAmountMap = savingsServiceImp.calculateGoalAllocatedAmountMap(yearMonth);

  log.info(DateUtils.formatForMonth(previousMonth) + " | " + month + " | " + DateUtils.formatForMonth(nextMonth));

  model.addAttribute("goals", goalServiceImp.getAllGoals());
  model.addAttribute("totalAllocated", totalAllocated);
  model.addAttribute("monthToAllocate", formattedDate);
  model.addAttribute("goalAllocatedAmountMap", goalAllocatedAmountMap);
  model.addAttribute("previousMonth", DateUtils.formatForMonth(previousMonth));
  model.addAttribute("nextMonth", DateUtils.formatForMonth(nextMonth));

  return "savings/allocate-savings";
 }


}

