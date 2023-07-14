package nl.marisabel.savings.classes;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.classes.TransactionServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Controller
@Log4j2
@RequestMapping("/savings")
public class GoalController {


 private final TransactionServiceImp transactionsService;
 private final GoalsAndSavingsService services;

 public GoalController(TransactionServiceImp transactionsService, GoalsAndSavingsService services) {
  this.transactionsService = transactionsService;
  this.services = services;
 }


 @GetMapping("/goals")
 public String showAllGoals(@ModelAttribute("goal") GoalEntity goal, Model model) {
  // load data
  List<GoalEntity> goals = services.getGoalService().getAllGoals();
  List<SavingsEntity> savings = services.getSavingsService().getAllSavings();
  Map<String, String> monthsYears = transactionsService.getDistinctMonthsAndYears();
  Map<String, Double> monthlySavings = services.getSavingsService().calculateMonthlySavings(savings);

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
  services.getGoalService().saveOrUpdate(goal);
  log.info((goal.getId() != null ? "Goal updated... " : "Goal saved... ") + goal.getName());
  return "redirect:/savings/goals";
 }



 @GetMapping("/goals/update")
 public String showUpdateForm(@RequestParam("id") long id, Model model, GoalEntity goal) {
  showAllGoals(goal, model);
  goal = services.getGoalService().getGoalById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid goal Id:" + id));
  model.addAttribute("goal", goal);
  log.info("Editing goal : " + goal.getName());
  return "savings/manage-goals";
 }


 @PostMapping("/update")
 public String updateCategory(@ModelAttribute("goal") GoalEntity goal) {
  services.getGoalService().saveOrUpdate(goal);
  return "savings/manage-goals";
 }



}

