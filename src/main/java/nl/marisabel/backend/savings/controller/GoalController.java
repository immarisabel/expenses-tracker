package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
 public String showAllGoals(@ModelAttribute("goal") GoalEntity goal, Model model) {
  // load data
  List<GoalEntity> goals = goalService.getAllGoals();
  List<SavingsEntity> savings = savingsService.getAllSavings();
  Map<String, String> monthsYears = transactionService.getDistinctMonthsAndYears();
  Map<String, Double> monthlySavings = savingsService.calculateMonthlySavings(savings); // Calculate monthly savings data

  model.addAttribute("goals", goals);
  model.addAttribute("savings", savings);
  model.addAttribute("monthsYears", monthsYears);
  model.addAttribute("monthlySavings", monthlySavings); // Pass monthly savings data to the template

  // save new goal
  model.addAttribute("goal", new GoalEntity());

  log.info("SAVINGS: " + savings.size());

  return "savings/manage-goals";
 }



 @PostMapping("/goals")
 public String addGoal(@ModelAttribute("goal") GoalEntity goal) {
  goalService.saveNewGoal(goal);
  log.info("Goal saved... " + goal.getName());
  return "redirect:/savings/goals";
 }


}

