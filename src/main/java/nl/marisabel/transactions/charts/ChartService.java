package nl.marisabel.transactions.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.classes.CategoryEntity;
import nl.marisabel.transactions.categories.classes.CategoryServiceImp;
import nl.marisabel.transactions.classes.TransactionEntity;
import nl.marisabel.transactions.classes.TransactionServiceImp;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChartService {

 private final TransactionServiceImp transactionService;
 private final CategoryServiceImp categoryService;
 private final String EXCLUDE_CATEGORY = "exclude";

 public ChartService(TransactionServiceImp transactionService, CategoryServiceImp categoryService) {
  this.transactionService = transactionService;
  this.categoryService = categoryService;
 }


 /**
  * This method calculates the monthly totals for a given year based on a list of transactions.
  * It filters the transactions based on the specified year and credit or debit type,
  * excludes transactions with a specific category, and then groups the remaining transactions by month,
  * summing the transaction amounts for each month.
  *
  * @param transactions
  * @param creditOrDebit
  * @return monthlyTotals
  */
 private Map<String, Double> calculateMonthlyTotals(List<TransactionEntity> transactions, String creditOrDebit) {
  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  for (TransactionEntity transaction : transactions) {
    String month = transaction.getDate().getMonth().toString();
    double amount = transaction.getAmount();
    String transactionCreditOrDebit = transaction.getCreditOrDebit();
    if (transactionCreditOrDebit.equalsIgnoreCase(creditOrDebit)) {
     monthlyTotals.merge(month, amount, Double::sum);
    }

  }
  return monthlyTotals;
 }
 public Map<String, Double> getMonthlyCreditsByCategoryForCategory(int year, CategoryEntity categoryEntity) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionService.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  return calculateMonthlyTotals(allTransactions, "CREDIT");
 }
 public Map<String, Double> getMonthlyDebitsByCategoryForCategory(int year, CategoryEntity categoryEntity) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<TransactionEntity> allTransactions = transactionService.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  return calculateMonthlyTotals(allTransactions, "DEBIT");
 }

 /**
  * with the filtered transactions and the specified year and debit type ("DEBIT").
  * It returns the monthly debit totals for the given year.
  *
  * @param transactions
  * @param year
  * @param creditOrDebit
  * @return
  */
 private Map<String, Double> calculateMonthlyTotalsForYear(List<TransactionEntity> transactions, int year, String creditOrDebit) {
  log.info(".... loading calculateMonthlyTotalsForYear()");
  log.info(".... for year " + year);

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

 /**
  * helper method that filters a list of transactions by excluding those with a specific category.
  * It takes a list of transactions as input and returns a new list that contains only the transactions
  * that do not have the excluded category.
  *
  * @param transactions
  * @return list of transactions without the excluded category
  */
 private List<TransactionEntity> filterTransactionsByCategories(List<TransactionEntity> transactions) {
  return transactions.stream()
          .filter(transaction -> transaction.getCategories().stream()
                  .noneMatch(category -> category.getCategory().equals(EXCLUDE_CATEGORY)))
          .collect(Collectors.toList());
 }
 public Map<String, Double> getMonthlyCreditsForYear(int year) {
  List<TransactionEntity> allTransactions = transactionService.findAll();
  List<TransactionEntity> filteredTransactions = filterTransactionsByCategories(allTransactions);
  log.info(".... loading getMonthlyCreditsForYear()");
  return calculateMonthlyTotalsForYear(filteredTransactions, year, "CREDIT");
 }
 public Map<String, Double> getMonthlyDebitsForYear(int year) {
  List<TransactionEntity> allTransactions = transactionService.findAll();
  List<TransactionEntity> filteredTransactions = filterTransactionsByCategories(allTransactions);
  log.info(".... loading getMonthlyDebitsForYear()");
  return calculateMonthlyTotalsForYear(filteredTransactions, year, "DEBIT");
 }

 /**
  * @param year
  * @param creditOrDebit
  * @return /charts/year?year=XXXX
  */
 public Map<String, Double> getMonthlyTotalsForYearlyChart(int year, String creditOrDebit) {
  List<CategoryEntity> categories = categoryService.getCategories();
  log.info(".... categories in database: " + categories.size());

  List<TransactionEntity> filteredTransactions = filterTransactionsByCategories(transactionService.findAll());

  log.info(".... loading getMonthlyTotalsForYearlyChart()");
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

 /**
  * @param categories
  * @param yearMonth
  * @param creditOrDebit
  * @return monthlyTotals /charts/month/{month}
  */
 private Map<String, Double> calculateMonthlyTotalsByCategory(List<CategoryEntity> categories, YearMonth yearMonth, String creditOrDebit) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  for (CategoryEntity category : categories) {

   List<TransactionEntity> transactions = transactionService.findAllByDateBetweenAndCategory(startDate, endDate, category);
   double total = transactions.stream()
           .filter(transaction -> transaction.getCreditOrDebit().equalsIgnoreCase(creditOrDebit))
           .mapToDouble(TransactionEntity::getAmount)
           .sum();

   monthlyTotals.put(category.getCategory(), total);
  }
  return monthlyTotals;
 }
 public Map<String, Double> getMonthlyCreditsByCategoryForMonth(YearMonth yearMonth) {
  List<CategoryEntity> categories = categoryService.getCategories();
  return calculateMonthlyTotalsByCategory(categories, yearMonth, "CREDIT");
 }
 public Map<String, Double> getMonthlyDebitsByCategoryForMonth(YearMonth yearMonth) {
  List<CategoryEntity> categories = categoryService.getCategories();
  return calculateMonthlyTotalsByCategory(categories, yearMonth, "DEBIT");
 }


}
