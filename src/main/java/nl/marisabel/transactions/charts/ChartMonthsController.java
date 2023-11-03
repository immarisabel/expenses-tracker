package nl.marisabel.transactions.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.classes.CategoryServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class ChartMonthsController {

 private final ChartService chartService;
 private final CategoryServiceImp categoryServiceImp;

 public ChartMonthsController(ChartService chartService, CategoryServiceImp categoryServiceImp) {
  this.chartService = chartService;
  this.categoryServiceImp = categoryServiceImp;
 }


 @GetMapping("/charts/month/{month}")
 public String showChartByMonthlyCategories(@PathVariable String month, Model model) {
  // Parse the month from the request
  log.info("Original YearMonth: " + month);

  if (month == null || month.isEmpty()) {
   YearMonth currentYearMonth = YearMonth.now();
   month = currentYearMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
  }

  // Original formatting
  DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMyyyy");
  YearMonth yearMonth = YearMonth.parse(month, monthFormatter);
  log.info("Parsed YearMonth: " + yearMonth);

  // Formatting for header
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
  String formattedDate = yearMonth.format(formatter);
  log.info("Formatted Date: " + formattedDate);

  // Calculate the previous and next months
  YearMonth previousMonth = yearMonth.minusMonths(1);
  YearMonth nextMonth = yearMonth.plusMonths(1);

  // Fetch the monthly totals for the given month
  Map<String, Double> monthlyCreditsByCategory = chartService.getMonthlyCreditsAndBijByCategoryForMonth(yearMonth);
  Map<String, Double> monthlyDebitsByCategory = chartService.getMonthlyDebitsAndAfByCategoryForMonth(yearMonth);

  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();
  List<Double> savings = new ArrayList<>();

  for (Map.Entry<String, Double> entry : monthlyCreditsByCategory.entrySet()) {
   String category = entry.getKey();
   double creditTotal = entry.getValue();
   double debitTotal = monthlyDebitsByCategory.getOrDefault(category, 0.0);

   labels.add(category);
   credits.add(creditTotal);
   debits.add(debitTotal);
   savings.add(creditTotal - debitTotal); // Compute savings for each category
  }

  log.info(previousMonth.format(monthFormatter) + " | " + month + " | " + nextMonth.format(monthFormatter));

  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("savings", savings);
  model.addAttribute("fulldate", formattedDate);
  model.addAttribute("month", month);
  model.addAttribute("previousMonth", previousMonth.format(monthFormatter));
  model.addAttribute("nextMonth", nextMonth.format(monthFormatter));

  return "charts/chart-monthly-categories";
 }
}
