package nl.marisabel.frontend.charts.service;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DashboardService {

 private final ChartService chartService;
 private final CategoryService categoryService;
 private final TransactionService transactionService;
 private final SavingsService savingsService;

 public DashboardService(ChartService chartService, CategoryService categoryService, TransactionService transactionService, SavingsService savingsService) {
  this.chartService = chartService;
  this.categoryService = categoryService;
  this.transactionService = transactionService;
  this.savingsService = savingsService;
 }



 public void showChartForCurrentYear(Model model) {
  int currentYear = Year.now().getValue();

  // Get data for the current year
  Map<String, Double> monthlyCredits = chartService.getMonthlyCreditsForYear(currentYear);
  Map<String, Double> monthlyDebits = chartService.getMonthlyDebitsForYear(currentYear);
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
// Extract month and year from labels
  List<String> monthYearLabels = new ArrayList<>();
  for (String label : labels) {
   String monthYear = label;
   monthYearLabels.add(monthYear);
  }

  log.info("monthYearLabels: " + monthYearLabels);

  // Add attributes to the model for use in the view
  model.addAttribute("labels", monthYearLabels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("currentYear", currentYear);

 }

 public void loadCategorizedTransactionsPrevMonth(Model model) {
  List<CategoryEntity> categories = categoryService.getCategories();
  List<TransactionEntity> transactions = transactionService.getAllTransactions();

  LocalDate currentDate = LocalDate.now();
  LocalDate previousMonth = currentDate.minusMonths(1);
  List<TransactionEntity> previousMonthTransactions = transactions.stream()
          .filter(transaction -> transaction.getDate().getMonth().equals(previousMonth.getMonth()))
          .collect(Collectors.toList());

  double totalAmountSpent = previousMonthTransactions.stream()
          .mapToDouble(TransactionEntity::getAmount)
          .sum();

  Map<String, Double> categoryPercentageMap = new HashMap<>();
  Map<String, Double> totalAmountMap = new HashMap<>();

  for (CategoryEntity category : categories) {
   double categoryAmountSpent = previousMonthTransactions.stream()
           .filter(transaction -> transaction.getCategories().contains(category))
           .mapToDouble(TransactionEntity::getAmount)
           .sum();
   double categoryPercentage = (categoryAmountSpent / totalAmountSpent) * 100;
   categoryPercentageMap.put(category.getCategory(), categoryPercentage);
   totalAmountMap.put(category.getCategory(), categoryAmountSpent);
  }

  model.addAttribute("categories", categories);
  model.addAttribute("totalAmountSpent", totalAmountSpent);
  model.addAttribute("totalAmountMap", totalAmountMap);
  model.addAttribute("categoryPercentageMap", categoryPercentageMap);
 }


}

