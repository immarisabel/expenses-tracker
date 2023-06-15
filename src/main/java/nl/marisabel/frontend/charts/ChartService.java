package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class ChartService {

 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 public ChartService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }


 // DATA SETS FOR ALL CHARTS

 public DefaultCategoryDataset generateExpenseIncomeDataset() {
  List<TransactionEntity> transactionEntities = transactionRepository.findAll();

  DefaultCategoryDataset dataset = new DefaultCategoryDataset();

  double totalIncome = 0;
  double totalExpenses = 0;

  for (TransactionEntity transaction : transactionEntities) {
   if (transaction.getCreditOrDebit().equalsIgnoreCase("CREDIT")) {
    totalIncome += transaction.getAmount();
   } else if (transaction.getCreditOrDebit().equalsIgnoreCase("DEBIT")) {
    totalExpenses += transaction.getAmount();
   }
  }

  dataset.addValue(totalIncome, "Income", "");
  dataset.addValue(totalExpenses, "Expenses", "");

  return dataset;
 }

 public String[] getCreditOrDebitArray() {
  List<String> creditOrDebitList = transactionRepository.findAll()
          .stream()
          .map(TransactionEntity::getCreditOrDebit)
          .toList();

  return creditOrDebitList.toArray(new String[0]);
 }

 public int[] getAmountsArray() {
  List<Integer> amountsList = transactionRepository.findAllAmounts();
  return amountsList.stream().mapToInt(Integer::intValue).toArray();
 }

 public int getTotalCredits() {
  return transactionRepository.calculateTotalCredits();
 }

 public int getTotalDebits() {
  return transactionRepository.calculateTotalDebits();
 }


 /////////////////////////////// CHARTS

 // CHART MONTHLY BALANCES

 public Map<String, Double> getMonthlyTotals() {
  List<TransactionEntity> allTransactions = transactionRepository.findAll();
  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  for (TransactionEntity transaction : allTransactions) {
   String month = transaction.getDate().getMonth().toString();
   double amount = transaction.getAmount();

   monthlyTotals.merge(month, amount, Double::sum);
  }

  return monthlyTotals;
 }


//  CHARTS FOR YEARLY DISPLAY IN MONTHS

 public Map<String, Double> getMonthlyCreditsForYear(int year) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionRepository.findByDateBetween(startDate, endDate);
  Map<String, Double> monthlyCredits = new LinkedHashMap<>();
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

  for (TransactionEntity transaction : allTransactions) {
   String monthYear = transaction.getDate().format(formatter);
   double amount = transaction.getAmount();
   String creditOrDebit = transaction.getCreditOrDebit();

   if ("credit".equalsIgnoreCase(creditOrDebit) || "bij".equalsIgnoreCase(creditOrDebit)) {
    monthlyCredits.merge(monthYear, amount, Double::sum);


   }
  }

  return monthlyCredits;
 }

 public Map<String, Double> getMonthlyDebitsForYear(int year) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionRepository.findByDateBetween(startDate, endDate);
  Map<String, Double> monthlyDebits = new LinkedHashMap<>();
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

  for (TransactionEntity transaction : allTransactions) {
   String monthYear = transaction.getDate().format(formatter);
   double amount = transaction.getAmount();
   String creditOrDebit = transaction.getCreditOrDebit();

   if ("debit".equalsIgnoreCase(creditOrDebit) || "af".equalsIgnoreCase(creditOrDebit)) {
    monthlyDebits.merge(monthYear, amount, Double::sum);


   }
  }

  return monthlyDebits;
 }


 // CHARTS MONTHLY   WITH CATEGORIES

 public Map<String, Double> getMonthlyCreditsByCategoryForMonth(YearMonth yearMonth) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  List<CategoryEntity> categories = categoryRepository.findAll();
  Map<String, Double> monthlyCredits = new LinkedHashMap<>();

  if (!categories.isEmpty()) {
   for (CategoryEntity category : categories) {
    List<TransactionEntity> transactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
    double total = transactions.stream()
            .filter(transaction -> ("credit".equalsIgnoreCase(transaction.getCreditOrDebit()) || "bij".equalsIgnoreCase(transaction.getCreditOrDebit())))
            .mapToDouble(TransactionEntity::getAmount)
            .sum();

    monthlyCredits.put(category.getCategory(), total);
   }
  }

  return monthlyCredits;
 }


 public Map<String, Double> getMonthlyDebitsByCategoryForMonth(YearMonth yearMonth) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  List<CategoryEntity> categories = categoryRepository.findAll();
  Map<String, Double> monthlyDebits = new LinkedHashMap<>();

  for (CategoryEntity category : categories) {
   List<TransactionEntity> transactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
   double total = transactions.stream()
           .filter(transaction -> ("debit".equalsIgnoreCase(transaction.getCreditOrDebit()) || "af".equalsIgnoreCase(transaction.getCreditOrDebit())))
           .mapToDouble(TransactionEntity::getAmount)
           .sum();

   monthlyDebits.put(category.getCategory(), total);

  }

  return monthlyDebits;
 }


// MONTHLY CHART FOR EACH INDIVIDUAL CATEGORY

 public Map<String, Double> getMonthlyCreditsByCategoryForCategory(int year, CategoryEntity categoryEntity) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();


  List<TransactionEntity> allTransactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  Map<String, Double> monthlyCredits = new LinkedHashMap<>();
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

  for (TransactionEntity transaction : allTransactions) {
   String monthYear = transaction.getDate().format(formatter);
   double amount = transaction.getAmount();
   String creditOrDebit = transaction.getCreditOrDebit();

   if ("credit".equalsIgnoreCase(creditOrDebit) || "bij".equalsIgnoreCase(creditOrDebit)) {
    monthlyCredits.merge(monthYear, amount, Double::sum);
   }
  }
  log.info("getMonthlyCreditsByCategoryForCategory : " + monthlyCredits);
  return monthlyCredits;
 }


 public Map<String, Double> getMonthlyDebitsByCategoryForCategory(int year, CategoryEntity categoryEntity) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  Map<String, Double> monthlyDebits = new LinkedHashMap<>();
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

  for (TransactionEntity transaction : allTransactions) {
   String monthYear = transaction.getDate().format(formatter);
   double amount = transaction.getAmount();
   String creditOrDebit = transaction.getCreditOrDebit();

   if ("debit".equalsIgnoreCase(creditOrDebit) || "af".equalsIgnoreCase(creditOrDebit)) {
    monthlyDebits.merge(monthYear, amount, Double::sum);
   }

  }
  return monthlyDebits;

 }
}
