package nl.marisabel.transactions.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.savings.classes.GoalEntity;
import nl.marisabel.savings.classes.SavingsEntity;
import nl.marisabel.savings.classes.GoalsAndSavingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequestMapping("/charts")
public class ChartSavingsController {
 private final GoalsAndSavingsService service;

 public ChartSavingsController(GoalsAndSavingsService service) {
  this.service = service;
 }


 @GetMapping("/savings/{goalId}")
 public String displaySavingsChartPerGoal(@PathVariable Long goalId,
                                          Model model) {
  log.info("looking for goal " + goalId);

  try {
   // Load the given goal ID
   GoalEntity goal = service.getGoalService().getGoalById(goalId).orElseThrow(() -> new Exception("Goal not found"));

   // Load all savings per month for the given goal
   List<SavingsEntity> savingsEntities = goal.getSavingsEntities();

   // Sort the savings by month and year
   savingsEntities.sort((a, b) -> {
    int yearComparison = a.getSavingYear().compareTo(b.getSavingYear());
    if (yearComparison == 0) {
     // If years are same, compare by month
     return a.getSavingsMonth().compareTo(b.getSavingsMonth());
    } else {
     return yearComparison;
    }
   });

   // Map the sorted list to the required formats and pass them to the model
   List<String> monthYearLabels = savingsEntities.stream()
           .map(savings -> savings.getSavingsMonth().toString() + '-' + savings.getSavingYear().toString())
           .collect(Collectors.toList());
   List<Double> savingsAmounts = savingsEntities.stream()
           .map(SavingsEntity::getAmount)
           .collect(Collectors.toList());

   model.addAttribute("goalName", goal.getName());
   model.addAttribute("monthYearLabels", monthYearLabels);
   model.addAttribute("savingsAmounts", savingsAmounts);

   // Log the required information
   log.info("GOAL: " + goal.getName());
   log.info("Amount so far: " + goal.getLastAmount());
   log.info("Size of the savings map: " + savingsEntities.size());

   return "charts/chart-monthly-savings";
  } catch (Exception e) {
   log.info("Error: " + e.getMessage());
   return "redirect:/savings/goals";
  }
 }
}

