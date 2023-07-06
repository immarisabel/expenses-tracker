package nl.marisabel.frontend.charts.service;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryServiceImp;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.service.SavingsServiceImp;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.UploadFileRepository;
import nl.marisabel.backend.transactions.service.TransactionServiceImp;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DashboardService {

 private final ChartService chartService;
 private final CategoryServiceImp categoryServiceImp;
 private final TransactionServiceImp transactionServiceImp;
 private final SavingsServiceImp savingsServiceImp;
 private final UploadFileRepository uploadFileRepository;

 public DashboardService(ChartService chartService, CategoryServiceImp categoryServiceImp, TransactionServiceImp transactionServiceImp, SavingsServiceImp savingsServiceImp, UploadFileRepository uploadFileRepository) {
  this.chartService = chartService;
  this.categoryServiceImp = categoryServiceImp;
  this.transactionServiceImp = transactionServiceImp;
  this.savingsServiceImp = savingsServiceImp;
  this.uploadFileRepository = uploadFileRepository;
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
  List<CategoryEntity> categories = categoryServiceImp.getCategories();
  List<TransactionEntity> transactions = transactionServiceImp.getAllTransactions();

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

 /**
  * CALCULATE EXPENSES AMOUNT OF PREVIOUS YEAR
  *
  * @return total amount calculated of transactions of the year -1 from current year as DEBIT
  * Example: today is 2023, last year was 2022
  */
 public String calculateExpensesAmountOfPrevYear() {
  Year year = Year.now();
  Year lastYear = year.minusYears(1);
  List<TransactionEntity> transactions = transactionServiceImp.getAllTransactions();
  List<TransactionEntity> previousYearTransactions = transactions.stream()
          .filter(transaction -> transaction.getDate().getYear() == lastYear.getValue())
          .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase("debit"))
          .collect(Collectors.toList());

  double totalAmountSpentLastYear = previousYearTransactions.stream()
          .mapToDouble(TransactionEntity::getAmount)
          .sum();

  DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  String formattedAmount = decimalFormat.format(totalAmountSpentLastYear);

  log.info("Total expenses prev year:" + formattedAmount);

  return formattedAmount;
 }

 /**
  * CALCULATE INCOME AMOUNT OF PREVIOUS YEAR
  *
  * @return total amount calculated of transactions of the year -1 from current year as CREDIT
  * Example: today is 2023, last year was 2022
  */
 public String calculateIncomeAmountOfPrevYear() {
  Year year = Year.now();
  Year lastYear = year.minusYears(1);
  List<TransactionEntity> transactions = transactionServiceImp.getAllTransactions();
  List<TransactionEntity> previousYearTransactions = transactions.stream()
          .filter(transaction -> transaction.getDate().getYear() == lastYear.getValue())
          .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase("credit"))
          .collect(Collectors.toList());

  double totalAmountSpentLastYear = previousYearTransactions.stream()
          .mapToDouble(TransactionEntity::getAmount)
          .sum();

  DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  String formattedAmount = decimalFormat.format(totalAmountSpentLastYear);

  log.info("Total income prev year:" + formattedAmount);

  return formattedAmount;
 }

 /**
  * GET LAST UPLOADED FILE DATE SIGNATURE
  * Calculates how many rows and provide the last indexed file
  *
  * @return String of Local Date from UploadFile Enity
  */
 public String getLastUploadedFileDateSignature() {

  int amountOfFilesUploaded = uploadFileRepository.findAll().size();
  log.info("Total uploads: " + amountOfFilesUploaded);

  if (amountOfFilesUploaded != 0) {
   String lastFileDate = String.valueOf(uploadFileRepository.findAll().get(amountOfFilesUploaded - 1).getSignatureDate());
   LocalDate date = LocalDate.parse(lastFileDate);
   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
   String formattedDate = date.format(formatter);
   return formattedDate;
  }
  return "upload a file first";
 }

 /**
  * get the grand total of savings
  * @return 00.00 String
  */
 public String getTotalSavings() {
  List<SavingsEntity> savings = savingsServiceImp.getAllSavings();
  double totalSavings = savings.stream()
          .mapToDouble(SavingsEntity::getAmount)
          .sum();
  DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  String formattedAmount = decimalFormat.format(totalSavings);
  log.info("savings: " + formattedAmount);
  return formattedAmount;
 }

}

