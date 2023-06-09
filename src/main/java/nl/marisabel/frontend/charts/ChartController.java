package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class ChartController {

 private final ChartService chartService;

 public ChartController(ChartService chartService) {
  this.chartService = chartService;
 }

 @GetMapping("/grandTotalChart")
 public String showChart(Model model) {
  int totalCredits = chartService.getTotalCredits();
  int totalDebits = chartService.getTotalDebits();

  log.info("CREDIT: "+ totalCredits + " DEBIT: " + totalDebits);

  // Pass data to the view
  model.addAttribute("totalCredits", totalCredits);
  model.addAttribute("totalDebits", totalDebits);
//TODO chart grand totals
  return "chart";
 }





 @GetMapping("/chart")
 public String showChartWithMonths(@RequestParam(value = "year", required = false) Integer year, Model model) {
  int currentYear = Year.now().getValue();

  if (year == null || year == 0) {
   year = currentYear;
  }

  log.info(".... the year is " + year);

  // Get data for the specified year
  Map<String, Double> monthlyCredits = chartService.getMonthlyCreditsForYear(year);
  Map<String, Double> monthlyDebits = chartService.getMonthlyDebitsForYear(year);

  // Generate labels and data for the chart
  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();

  for (Map.Entry<String, Double> entry : monthlyCredits.entrySet()) {
   String month = entry.getKey();
   double creditTotal = monthlyCredits.getOrDefault(month, 0.0);
   double debitTotal = monthlyDebits.getOrDefault(month, 0.0);

   labels.add(month);
   credits.add(creditTotal);
   debits.add(debitTotal);
  }

  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("currentYear", year);

  // Set previous and next year for pagination
  int previousYear = year - 1;
  int nextYear = year + 1;
  model.addAttribute("prevYear", previousYear);
  model.addAttribute("nextYear", nextYear);

  return "charts/chart-months";
 }









}
