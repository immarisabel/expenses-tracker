package nl.marisabel.frontend.dashboard;

import nl.marisabel.frontend.charts.ChartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

 private final ChartService chartService;

 public DashboardController(ChartService chartService) {
  this.chartService = chartService;
 }

 @GetMapping
 public String dashboard(@RequestParam(value = "year", required = false) Integer year, Model model){
  showChartWithMonths(year, model);
  return "dashboard/dashboard";
 }


 public void showChartWithMonths(Integer year, Model model) {
  int currentYear = Year.now().getValue();
  if (year == null || year == 0) {
   year = currentYear;
  }

  // Get data for the specified year
  Map<String, Double> monthlyCredits = chartService.getMonthlyCreditsForYear(year);
  Map<String, Double> monthlyDebits = chartService.getMonthlyDebitsForYear(year);

  // Generate labels and data for the chart
  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();

  // Populate the labels, credits, and debits lists
  for (Map.Entry<String, Double> entry : monthlyCredits.entrySet()) {
   String month = entry.getKey();
   double creditTotal = monthlyCredits.getOrDefault(month, 0.0);
   double debitTotal = monthlyDebits.getOrDefault(month, 0.0);

   labels.add(month);
   credits.add(creditTotal);
   debits.add(debitTotal);
  }

  // Add attributes to the model for use in the view
  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("currentYear", year);

  // Set previous and next year for pagination
  int previousYear = year - 1;
  int nextYear = year + 1;
  model.addAttribute("prevYear", previousYear);
  model.addAttribute("nextYear", nextYear);

 }
}





// GET
// earnings overview lineal
// savings oveview lineal
// expenses overview lineal
// combined

// last months categories amount table or pie?


