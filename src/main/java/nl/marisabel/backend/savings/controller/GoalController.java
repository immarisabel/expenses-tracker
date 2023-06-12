package nl.marisabel.backend.savings.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.service.GoalService;
import nl.marisabel.backend.savings.service.SavingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
  model.addAttribute("goal", goal);
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

