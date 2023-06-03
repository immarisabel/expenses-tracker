package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequestMapping("/chart")
public class ChartController {

 private final ChartService chartService;

 public ChartController(ChartService chartService) {
  this.chartService = chartService;
 }

 @GetMapping
 public String showChart(Model model) {
  int totalCredits = chartService.getTotalCredits();
  int totalDebits = chartService.getTotalDebits();

  log.info("CREDIT: "+ totalCredits + " DEBIT: " + totalDebits);

  // Pass data to the view
  model.addAttribute("totalCredits", totalCredits);
  model.addAttribute("totalDebits", totalDebits);

  return "chart";
 }


 @GetMapping("/months")
 public String showChartWithMonths(Model model) {
  Map<String, Double> monthlyTotals = chartService.getMonthlyTotals();

  // Extract the months and totals from the map
  List<String> labels = new ArrayList<>();
  List<Double> totals = new ArrayList<>();

  for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
   String month = entry.getKey();
   double total = entry.getValue();
   labels.add(month);
   totals.add(total);
  }

  // Pass data to the view
  model.addAttribute("labels", labels);
  model.addAttribute("totals", totals);

  return "chart-months";
 }




}
