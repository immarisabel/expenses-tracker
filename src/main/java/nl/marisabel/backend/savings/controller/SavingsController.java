package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.error.ResourceNotFoundException;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.savings.SavingsModel;
import nl.marisabel.util.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/savings")
public class SavingsController {

 private final SavingsService savingsService;
 private final TransactionService transactionService;
 private final GoalService goalService;

 public SavingsController(SavingsService savingsService, TransactionService transactionService, GoalService goalService) {
  this.savingsService = savingsService;
  this.transactionService = transactionService;
  this.goalService = goalService;
 }


 @PostMapping("/allocate-savings/{month}")
 public String allocateSavings(@PathVariable String month,
                               @RequestBody List<SavingsModel> savingsDTOs,
                               RedirectAttributes redirectAttributes) {

  YearMonth yearMonth = DateUtils.parseToYearMonth(month);

  for (SavingsModel dto : savingsDTOs) {
   GoalEntity goal = goalService.getGoalById(dto.getGoalId())
           .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + dto.getGoalId()));
   goalService.updateGoalState(goal, dto.getAmount());
   savingsService.updateSavings(goal, yearMonth, dto.getAmount());
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

  double totalAllocated = savingsService.calculateTotalAllocated(yearMonth);
  Map<Long, Double> goalAllocatedAmountMap = savingsService.calculateGoalAllocatedAmountMap(yearMonth);

  double difference = transactionService.calculateRemainingFundsByMonth(startOfMonth, endOfMonth);
  log.info("REMAINING FUNDS FOR : " + formattedDate + " [ " + difference + " ] ");
  double totalToAllocate = Math.round(difference * 100.00) / 100.00;
  DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  String roundedTotal = decimalFormat.format(totalToAllocate);

  log.info(DateUtils.formatForMonth(previousMonth) + " | " + month + " | " + DateUtils.formatForMonth(nextMonth));

  model.addAttribute("goals", goalService.getAllGoals());
  model.addAttribute("totalToAllocate", roundedTotal);
  model.addAttribute("totalAllocated", totalAllocated);
  model.addAttribute("monthToAllocate", formattedDate);
  model.addAttribute("goalAllocatedAmountMap", goalAllocatedAmountMap);
  model.addAttribute("previousMonth", DateUtils.formatForMonth(previousMonth));
  model.addAttribute("nextMonth", DateUtils.formatForMonth(nextMonth));

  return "savings/allocate-savings";
 }



}

