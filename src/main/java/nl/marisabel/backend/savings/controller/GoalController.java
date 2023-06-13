package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Log4j2
public class GoalController {

 private final SavingsService savingsService;
 private final GoalService goalService;


 public GoalController(SavingsService savingsService, GoalService goalService) {
  this.savingsService = savingsService;
  this.goalService = goalService;
 }


 @GetMapping("/savings/goals")
 public String showAllGoals(@ModelAttribute ("goal") GoalEntity goal, Model model) {

  // load data
  List<GoalEntity> goals = goalService.getAllGoals();
  List<SavingsEntity> savings = savingsService.getAllSavings();
  model.addAttribute("goals", goals);
  model.addAttribute("savings", savings);

  log.info("SAVINGS: " + savings.size());


  // save new goal
  model.addAttribute("goal", new GoalEntity());
  return "savings/new-goal";
 }


 @PostMapping("/savings/goals")
 public String addGoal(@ModelAttribute ("goal") GoalEntity goal) {
  goalService.saveNewGoal(goal);
  log.info("Goal saved... "+ goal.getName());
  return "redirect:/savings/goals";
 }


 // display charts
 // display goals list


}

