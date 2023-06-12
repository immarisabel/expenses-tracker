package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.savings.GoalModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class SavingsController {

 private final SavingsService savingsService;
 private final TransactionService transactionService;

 public SavingsController(SavingsService savingsService, TransactionService transactionService) {
  this.savingsService = savingsService;
  this.transactionService = transactionService;
 }

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
  List<GoalModel> goals = new ArrayList<>();
  goals.add(new GoalModel("Insurance", 150.00, 0, " ", false));
  goals.add(new GoalModel("House", 150.00, 0, " ", false));
  goals.add(new GoalModel("Clothes", 150.00, 0, " ", false));


  model.addAttribute("goals", goals);
  model.addAttribute("totalToAllocate", roundedTotal);
  model.addAttribute("totalAllocated", totalAllocated);
  model.addAttribute("monthToAllocate", formattedDate);
  //TODO
  // if ((credit - debit)>0){
  // load in amount to be allocated controller.
  // load goals
  // then allocate amounts per goal
  // if(allocating > toAllocate) {
  // return error - you do not have enough
  // }  else{
  // store in transactions allocated }
  // }

  return "goals";
 }


 // edit goals - add amount and name, remove, edit amount and name


 // METHODS I NEED (or controllers or service WTEVER):
 // - automate amount to distruibute
 // - save a new goal
 // - save the amounts allocated to savings
 // - display chart of savings per month
 // - display amount saved TOTAL next to each goal
 // PAGES I NEED:
 // - goals settings (make, edit, delete)
 // - goals overview (table with totals and descriptions, and if reached)
 // - goal chart (per ID) (graphic not bars - per month)
 // - left over per month chart (graphic, not bars)
 // - savings allocation (sliders, in progress)
}

