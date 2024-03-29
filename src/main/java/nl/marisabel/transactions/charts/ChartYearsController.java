package nl.marisabel.transactions.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.classes.CategoryServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@Log4j2
public class ChartYearsController {

 private final ChartService chartService;
 private final CategoryServiceImp categoryServiceImp;

 public ChartYearsController(ChartService chartService, CategoryServiceImp categoryServiceImp) {
  this.chartService = chartService;
  this.categoryServiceImp = categoryServiceImp;
 }

 /**
  * loads current year if parameter is 0
  * @param year
  * @param model
  * @return  /charts/year & /charts/year?year=XXXX
  */
 @GetMapping("/charts/year")
 public String showChartWithMonths(@RequestParam(value = "year", required = false) Integer year, Model model) {
  // Get the current year
  int currentYear = Year.now().getValue();
  if (year == null || year == 0) {
   year = currentYear;
  }
  log.info(".... the year is " + year);
  // Get data for the specified year
  Map<String, Double> monthlyCredits = chartService.getMonthlyTotalsForYearlyChart(year, "CREDIT");
  Map<String, Double> monthlyDebits = chartService.getMonthlyTotalsForYearlyChart(year, "DEBIT");


  // Generate labels and data for the chart
  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();

  // Populate the labels, credits, and debits lists
  for (int month = 1; month <= 12; month++) {
   String monthLabel = Month.of(month).name();
   LocalDate date = LocalDate.of(currentYear, month, 1);
   String monthName = date.getMonth().name();
   labels.add(monthName);
   credits.add(monthlyCredits.getOrDefault(monthLabel, 0.0));
   debits.add(monthlyDebits.getOrDefault(monthLabel, 0.0));
  }
  List<String> monthYearLabels = new ArrayList<>();
  for (String label : labels) {
   String monthYear = label;
   monthYearLabels.add(monthYear);
  }
  // Add attributes to the model for use in the view
  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("currentYear", year);


  // totals for the current year
  String totalCredits = String.valueOf(chartService.sumTotalForYearlyChart(year, "CREDIT"));
  String totalDebits = String.valueOf(chartService.sumTotalForYearlyChart(year, "DEBIT"));

// Parse the strings to doubles
  double creditsAmount = Double.parseDouble(totalCredits);
  double debitsAmount = Double.parseDouble(totalDebits);

// Create a NumberFormat instance for Euros with 2 decimal places
  NumberFormat euroFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
  euroFormat.setMaximumFractionDigits(2);

// Format the amounts as Euros
  totalCredits = euroFormat.format(creditsAmount);
  totalDebits = euroFormat.format(debitsAmount);

  log.info("total credits: " + totalCredits);
  log.info("total debits: " + totalDebits);
  model.addAttribute("totalCredits", totalCredits);
  model.addAttribute("totalDebits", totalDebits);

  // Set previous and next year for pagination
  int previousYear = year - 1;
  int nextYear = year + 1;
  model.addAttribute("prevYear", previousYear);
  model.addAttribute("nextYear", nextYear);

  // Return the name of the view template
  return "charts/chart-years";
 }

}
