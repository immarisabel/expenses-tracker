package nl.marisabel.frontend.dashboard;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.SavingsService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.charts.ChartService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;
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
  log.info("credits map populated with data: KEY " + monthlyCredits.keySet() + " VALUE " + monthlyCredits.values());
  log.info("debits map populated with data: KEY " + monthlyDebits.keySet() + " VALUE " + monthlyDebits.values());

// Generate labels and data for the chart
  List<String> labels = new ArrayList<>();
  List<Double> credits = new ArrayList<>();
  List<Double> debits = new ArrayList<>();

// Populate the labels, credits, and debits lists
  for (int month = 1; month <= 12; month++) {
   String monthLabel = String.format("%02d%04d", month, currentYear);

   double creditTotal = monthlyCredits.getOrDefault(monthLabel, 0.0);
   double debitTotal = monthlyDebits.getOrDefault(monthLabel, 0.0);

   labels.add(monthLabel);
   credits.add(creditTotal);
   debits.add(debitTotal);
  }


  log.info("labels: " + labels);
  log.info("credits for labels: " + credits);
  log.info("debits for labels: " + debits);



  // Add attributes to the model for use in the view
  model.addAttribute("labels", labels);
  model.addAttribute("credits", credits);
  model.addAttribute("debits", debits);
  model.addAttribute("currentYear", currentYear);

  loadTotalSavingsPerMonth(model);
 }




 public void loadTotalSavingsPerMonth(Model model) {

  List<SavingsEntity> savings = savingsService.getAllSavings();
  Map<String, String> monthsYears = transactionService.getDistinctMonthsAndYears();
  Map<String, Double> monthlySavings = savingsService.calculateMonthlySavings(savings); // Calculate monthly savings data

  model.addAttribute("savings", savings);
  model.addAttribute("monthsYears", monthsYears);
  model.addAttribute("monthlySavings", monthlySavings); // Pass monthly savings data to the template

 }


 public void loadCategorizedTransactions(Model model) {
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
  for (CategoryEntity category : categories) {
   double categoryAmountSpent = previousMonthTransactions.stream()
           .filter(transaction -> transaction.getCategories().contains(category))
           .mapToDouble(TransactionEntity::getAmount)
           .sum();
   double categoryPercentage = (categoryAmountSpent / totalAmountSpent) * 100;
   categoryPercentageMap.put(category.getCategory(), categoryPercentage);
  }

  model.addAttribute("categories", categories);
  model.addAttribute("totalAmountSpent", totalAmountSpent);
  model.addAttribute("categoryPercentageMap", categoryPercentageMap);
 }


}

