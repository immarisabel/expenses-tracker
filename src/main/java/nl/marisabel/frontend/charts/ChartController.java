package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class ChartController {

 private final ChartService chartService;
 private final CategoryService categoryService;

 public ChartController(ChartService chartService, CategoryService categoryService) {
  this.chartService = chartService;
  this.categoryService = categoryService;
 }


 @GetMapping("/charts")
 public String showChartsMenu(Model model) {

  YearMonth currentYearMonth = YearMonth.now();
  String month = currentYearMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
  log.info(".... link generated for categories chart charts/" + month);

  List<CategoryEntity> categories = categoryService.getCategories();

  model.addAttribute("chartsMonthLink", "/charts/month/" + month);

  model.addAttribute("categories", categories);
  return "charts/charts";
 }


 @GetMapping("/charts/grandTotalChart")
 public String showChart(Model model) {
  int totalCredits = chartService.getTotalCredits();
  int totalDebits = chartService.getTotalDebits();

  log.info("CREDIT: " + totalCredits + " DEBIT: " + totalDebits);

  // Pass data to the view
  model.addAttribute("totalCredits", totalCredits);
  model.addAttribute("totalDebits", totalDebits);
//TODO chart grand totals
  return "chart";
 }










 @GetMapping("/charts/year")
 public String showChartWithMonths(@RequestParam(value = "year", required = false) Integer year, Model model) {
  // Get the current year
  int currentYear = Year.now().getValue();

  // If the year is not specified or 0, use the current year
  if (year == null || year == 0) {
   year = currentYear;
  }

  // Logging the selected year
  log.info(".... the year is " + year);

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

  // Return the name of the view template
  return "charts/chart-months";
 }



}
