package nl.marisabel.frontend.dashboard;

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
import java.util.stream.Collectors;

@Service
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

  loadTotalSavingsPerMonth(model); // Call the method to load total savings per month
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
  LocalDate previousMonth = LocalDate.of(2017,2,1);


  // Filter transactions for the previous month
//  LocalDate currentDate = LocalDate.now();
//  LocalDate previousMonth = currentDate.minusMonths(1);
//  List<TransactionEntity> previousMonthTransactions = transactions.stream()
//          .filter(transaction -> transaction.getDate().getMonth().equals(previousMonth.getMonth()))
//          .collect(Collectors.toList());

  List<TransactionEntity> previousMonthTransactions = transactions.stream()
          .filter(transaction -> transaction.getDate().getMonth().equals(previousMonth.getMonth()))
          .collect(Collectors.toList());
  // Calculate the total amount spent in the previous month
  double totalAmountSpent = previousMonthTransactions.stream()
          .mapToDouble(TransactionEntity::getAmount)
          .sum();

  // Calculate the percentage spent for each category

  Map<String, Double> categoryPercentageMap = new HashMap<>();
  for (CategoryEntity category : categories) {
   double categoryAmountSpent = previousMonthTransactions.stream()
           .filter(transaction -> transaction.getCategories().equals(category))
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

