package nl.marisabel.transactions.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.classes.CategoryEntity;
import nl.marisabel.transactions.categories.classes.CategoryServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.NumberFormat;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

@Log4j2
@Controller
public class ChartCategoriesController {
 private final CategoryServiceImp categoryServiceImp;
 private final ChartService chartService;

 @Autowired
 public ChartCategoriesController(CategoryServiceImp categoryServiceImp, ChartService chartService) {
  this.categoryServiceImp = categoryServiceImp;
  this.chartService = chartService;
 }

 @GetMapping("/charts/categories/{categoryId}")
 public String showCategoryCharts(@PathVariable Long categoryId,
                                  @RequestParam(value = "year", required = false) Integer year,
                                  Model model) {

  CategoryEntity category = categoryServiceImp.getCategory(categoryId);

  int currentYear = Year.now().getValue();

  // If the category is not found, return an error view
  if (category == null) {
   return "error";
  }

  // If the year is not specified or 0, use the current year
  if (year == null || year == 0) {
   year = currentYear;
  }

  // Logging category and year information
  log.info("Category: " + category.getCategory());
  log.info(".... the year is " + year);

  // Get data for the specified category and year
  YearMonth startYearMonth = YearMonth.of(year, 1);
  YearMonth endYearMonth = YearMonth.of(year, 12);

  // Get data for the specified year
  Map<String, Double> monthlyCredits = chartService.getMonthlyCreditsAndBijByCategoryForCategory(year, category);
  Map<String, Double> monthlyDebits = chartService.getMonthlyDebitsAndAfByCategoryForCategory(year, category);
  log.info("DEBITS " + monthlyDebits.size() + " | " + monthlyDebits.values()+ " | " +monthlyDebits.keySet());
  log.info("CREDITS " + monthlyCredits.size() + " | " + monthlyCredits.values()+ " | " +monthlyCredits.keySet());
  // Initialize lists to store chart data
  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();

// Populate the labels, credits, and debits lists
  for (int month = 1; month <= 12; month++) {
   Month monthEnum = Month.of(month);
   String monthLabel = monthEnum.name();
   String monthKey = monthLabel.toUpperCase() + year; // Concatenate month and year to create the key

   double creditTotal = monthlyCredits.getOrDefault(monthEnum.toString(), 0.0);
   double debitTotal = monthlyDebits.getOrDefault(monthEnum.toString(), 0.0);
   log.info("mapped: " +month+ monthKey + monthLabel + " | " + creditTotal + " | " + debitTotal);

   labels.add(monthLabel);
   credits.add(creditTotal);
   debits.add(debitTotal);
  }

  // Add attributes to the model for use in the view
  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("category", category);
  model.addAttribute("currentYear", year);





  // totals for the current year
  String totalCredits = String.valueOf(chartService.calculateYearTotalForCategoryCredits(year, category));
  String totalDebits = String.valueOf(chartService.calculateYearTotalForCategoryDebits(year, category));
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
  return "charts/chart-yearly-categories";
 }


}

