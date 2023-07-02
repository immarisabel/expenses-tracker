package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChartService {

 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;
 private final String EXCLUDE_CATEGORY = "exclude";

 public ChartService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }

 // Common method to filter transactions by credit or debit type
 private List<TransactionEntity> filterTransactionsByCreditOrDebit(String creditOrDebit) {
  return transactionRepository.findAll()
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
          .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase(creditOrDebit))
          .toList();
 }

 // Common method to calculate monthly totals
 private Map<String, Double> calculateMonthlyTotals(List<TransactionEntity> transactions, String creditOrDebit) {
  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  for (TransactionEntity transaction : transactions) {
   if (!transaction.getCategories().equals(EXCLUDE_CATEGORY)) {
    String month = transaction.getDate().getMonth().toString();
    double amount = transaction.getAmount();
    String transactionCreditOrDebit = transaction.getCreditOrDebit();

    if (transactionCreditOrDebit.equalsIgnoreCase(creditOrDebit)) {
     monthlyTotals.merge(month, amount, Double::sum);
    }
   }
  }
  log.info("calculateMonthlyTotals : " + monthlyTotals);
  return monthlyTotals;
 }


 // Common method to calculate monthly totals by category
 private Map<String, Double> calculateMonthlyTotalsByCategory(List<CategoryEntity> categories, YearMonth yearMonth, String creditOrDebit) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  for (CategoryEntity category : categories) {
   if (category.getCategory().equalsIgnoreCase(EXCLUDE_CATEGORY)) {
    continue;
   }


   List<TransactionEntity> transactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
   double total = transactions.stream()
           .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase(creditOrDebit))
           .mapToDouble(TransactionEntity::getAmount)
           .sum();

   monthlyTotals.put(category.getCategory(), total);
  }
  log.info("calculateMonthlyTotalsByCategory : " + monthlyTotals + "cat excluded: " + EXCLUDE_CATEGORY);

  return monthlyTotals;
 }


 // DATA SETS FOR ALL CHARTS

 public DefaultCategoryDataset generateExpenseIncomeDataset() {
  List<TransactionEntity> transactionEntities = transactionRepository.findAll();

  DefaultCategoryDataset dataset = new DefaultCategoryDataset();

  double totalIncome = filterTransactionsByCreditOrDebit("CREDIT")
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
          .mapToDouble(TransactionEntity::getAmount)
          .sum();

  double totalExpenses = filterTransactionsByCreditOrDebit("DEBIT")
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
          .mapToDouble(TransactionEntity::getAmount)
          .sum();

  dataset.addValue(totalIncome, "Income", "");
  dataset.addValue(totalExpenses, "Expenses", "");

  log.info("generateExpenseIncomeDataset : " + dataset);

  return dataset;
 }


 public String[] getCreditOrDebitArray() {
  List<String> creditOrDebitList = filterTransactionsByCreditOrDebit("CREDIT")
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
          .map(TransactionEntity::getCreditOrDebit)
          .toList();

  String array[] = creditOrDebitList.toArray(new String[0]);

  log.info("getCreditOrDebitArray : " + array);

  return array;
 }

 public int[] getAmountsArray() {
  List<Double> amountsList = transactionRepository.findAll()
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY)) // Exclude "transfer" category
          .map(TransactionEntity::getAmount)
          .collect(Collectors.toList());
  log.info(amountsList);
  return amountsList.stream().mapToInt(Double::intValue).toArray();
 }

 public int getTotalCredits() {
  return filterTransactionsByCreditOrDebit("CREDIT")
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY)) // Exclude "transfer" category
          .toList()
          .size();
 }

 public int getTotalDebits() {
  return filterTransactionsByCreditOrDebit("DEBIT")
          .stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY)) // Exclude "transfer" category
          .toList()
          .size();
 }


 /////////////////////////////// CHARTS

 // CHART MONTHLY BALANCES

 public Map<String, Double> getMonthlyTotals() {
  List<TransactionEntity> allTransactions = transactionRepository.findAll();
  log.info(".... loading getMonthlyTotals()");
  return calculateMonthlyTotals(allTransactions, "");
 }


// CHARTS FOR YEARLY DISPLAY IN MONTHS
public Map<String, Double> getMonthlyCreditsForYear(int year) {
 List<TransactionEntity> allTransactions = transactionRepository.findAll();
 List<TransactionEntity> filteredTransactions = allTransactions.stream()
         .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
         .collect(Collectors.toList());
 log.info(".... loading getMonthlyCreditsForYear()");
 return calculateMonthlyTotalsForYear(filteredTransactions, year, "CREDIT");
}

 public Map<String, Double> getMonthlyDebitsForYear(int year) {
  List<TransactionEntity> allTransactions = transactionRepository.findAll();
  List<TransactionEntity> filteredTransactions = allTransactions.stream()
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
          .collect(Collectors.toList());
  log.info(".... loading getMonthlyDebitsForYear()");
  return calculateMonthlyTotalsForYear(filteredTransactions, year, "DEBIT");
 }
 private Map<String, Double> calculateMonthlyTotalsForYear(List<TransactionEntity> transactions, int year, String creditOrDebit) {
  log.info(".... loading calculateMonthlyTotalsForYear()");
  log.info(".... for year " +year);
  return transactions.stream()
          .filter(transaction -> transaction.getDate().getYear() == year)
          .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase(creditOrDebit))
          .filter(transaction -> !transaction.getCategories().equals(EXCLUDE_CATEGORY))
          .collect(Collectors.groupingBy(
                  transaction -> transaction.getDate().getMonth().toString(),
                  LinkedHashMap::new,
                  Collectors.summingDouble(TransactionEntity::getAmount)
          ));
 }

 public Map<String, Double> getMonthlyTotalsForYearMainChart(int year, String creditOrDebit) {
  List<CategoryEntity> categories = categoryRepository.findAll();
  List<TransactionEntity> allTransactions = transactionRepository.findAll();
  log.info(".... original transactions: " + allTransactions.size());

  List<TransactionEntity> filteredTransactions = allTransactions.stream()
          .filter(transaction -> !transaction.getCategories().contains(EXCLUDE_CATEGORY))
          .collect(Collectors.toList());

  log.info(".... transactions with excluded category: " +
          filteredTransactions.stream().anyMatch(transaction -> transaction.getCategories().contains(EXCLUDE_CATEGORY))
          + " | categories in database: " + categories.size() + " | "
          + categories.stream().filter(category -> category.getCategory().equals(EXCLUDE_CATEGORY)).toList().size()+ " match " + EXCLUDE_CATEGORY);

  log.info(".... loading getMonthlyTotalsForYearMainChart()");
  log.info(".... filtered transactions: " + filteredTransactions.size());
  log.info(".... for year " + year);
  log.info(".... creditOrDebit: " + creditOrDebit);

  return filteredTransactions.stream()
          .filter(transaction -> transaction.getDate().getYear() == year)
          .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase(creditOrDebit))
          .collect(Collectors.groupingBy(
                  transaction -> transaction.getDate().getMonth().toString(),
                  LinkedHashMap::new,
                  Collectors.summingDouble(TransactionEntity::getAmount)
          ));
 }





 // CHARTS MONTHLY WITH CATEGORIES

 public Map<String, Double> getMonthlyCreditsByCategoryForMonth(YearMonth yearMonth) {
  List<CategoryEntity> categories = categoryRepository.findAll();
  return calculateMonthlyTotalsByCategory(categories, yearMonth, "CREDIT");
 }

 public Map<String, Double> getMonthlyDebitsByCategoryForMonth(YearMonth yearMonth) {
  List<CategoryEntity> categories = categoryRepository.findAll();
  return calculateMonthlyTotalsByCategory(categories, yearMonth, "DEBIT");
 }


 // MONTHLY CHART FOR EACH INDIVIDUAL CATEGORY

 public Map<String, Double> getMonthlyCreditsByCategoryForCategory(int year, CategoryEntity categoryEntity) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  return calculateMonthlyTotals(allTransactions, "CREDIT");
 }

 public Map<String, Double> getMonthlyDebitsByCategoryForCategory(int year, CategoryEntity categoryEntity) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionRepository.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  return calculateMonthlyTotals(allTransactions, "DEBIT");
 }
}
