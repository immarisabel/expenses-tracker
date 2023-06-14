package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;


@Controller
@Log4j2
@RequestMapping("/savings")
public class GoalController {

 private final SavingsService savingsService;
 private final GoalService goalService;
 private final TransactionService transactionService;


 public GoalController(SavingsService savingsService, GoalService goalService, TransactionService transactionService) {
  this.savingsService = savingsService;
  this.goalService = goalService;
  this.transactionService = transactionService;
 }


 @GetMapping("/goals")
 public String showAllGoals(@ModelAttribute ("goal") GoalEntity goal, Model model) {

  // load data
  List<GoalEntity> goals = goalService.getAllGoals();
  List<SavingsEntity> savings = savingsService.getAllSavings();
  List<TransactionEntity> transactions = transactionService.getAllTransactions();
  model.addAttribute("goals", goals);
  model.addAttribute("savings", savings);

  log.info("SAVINGS: " + savings.size());

  // Generate distinct months and years from transactions
  Map<String, String> monthsYears = transactions.stream()
          .map(t -> YearMonth.from(t.getDate()))
          .distinct()
          .sorted()
          .collect(Collectors.toMap(
                  ym -> ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                  ym -> ym.format(DateTimeFormatter.ofPattern("MMyyyy")),
                  (a, b) -> b,
                  LinkedHashMap::new));

  model.addAttribute("monthsYears", monthsYears);


  // save new goal
  model.addAttribute("goal", new GoalEntity());
  return "savings/manage-goals";
 }


 @PostMapping("/goals")
 public String addGoal(@ModelAttribute ("goal") GoalEntity goal) {
  goalService.saveNewGoal(goal);
  log.info("Goal saved... "+ goal.getName());
  return "redirect:/savings/goals";
 }


 // display charts
 // display goals list


}

