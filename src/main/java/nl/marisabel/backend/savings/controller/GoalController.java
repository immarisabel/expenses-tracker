package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.service.TransactionServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Controller
@Log4j2
@RequestMapping("/savings")
public class GoalController {

 private final SavingsService savingsService;
 private final GoalService goalService;
 private final TransactionServiceImp transactionServiceImp;


 public GoalController(SavingsService savingsService, GoalService goalService, TransactionServiceImp transactionServiceImp) {
  this.savingsService = savingsService;
  this.goalService = goalService;
  this.transactionServiceImp = transactionServiceImp;
 }

 @GetMapping("/goals")
 public String showAllGoals(@ModelAttribute("goal") GoalEntity goal, Model model) {
  // load data
  List<GoalEntity> goals = goalService.getAllGoals();
  List<SavingsEntity> savings = savingsService.getAllSavings();
  Map<String, String> monthsYears = transactionServiceImp.getDistinctMonthsAndYears();
  Map<String, Double> monthlySavings = savingsService.calculateMonthlySavings(savings);

  model.addAttribute("goals", goals);
  model.addAttribute("savings", savings);
  model.addAttribute("monthsYears", monthsYears);
  model.addAttribute("monthlySavings", monthlySavings);

  // save new goal
  model.addAttribute("goal", new GoalEntity());

  log.info("SAVINGS: " + savings.size());

  return "savings/manage-goals";
 }



 @PostMapping("/goals")
 public String addOrUpdateGoal(@ModelAttribute("goal") GoalEntity goal) {
  goalService.saveOrUpdate(goal);
  log.info((goal.getId() != null ? "Goal updated... " : "Goal saved... ") + goal.getName());
  return "redirect:/savings/goals";
 }



 @GetMapping("/goals/update")
 public String showUpdateForm(@RequestParam("id") long id, Model model, GoalEntity goal) {
  showAllGoals(goal, model);
  goal = goalService.getGoalById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid goal Id:" + id));
  model.addAttribute("goal", goal);
  log.info("Editing goal : " + goal.getName());
  return "savings/manage-goals";
 }


 @PostMapping("/update")
 public String updateCategory(@ModelAttribute("goal") GoalEntity goal) {
  goalService.saveOrUpdate(goal);
  return "savings/manage-goals";
 }



}

