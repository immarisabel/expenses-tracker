package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.repository.ExpenseRepository;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

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

 private final ExpenseRepository expenseRepository;
 private final CategoryRepository categoryRepository;

 public ChartService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
  this.expenseRepository = expenseRepository;
  this.categoryRepository = categoryRepository;
 }


 // DATA SETS FOR ALL CHARTS

 public DefaultCategoryDataset generateExpenseIncomeDataset() {
  List<ExpenseEntity> expenses = expenseRepository.findAll();

  DefaultCategoryDataset dataset = new DefaultCategoryDataset();

  double totalIncome = 0;
  double totalExpenses = 0;

  for (ExpenseEntity expense : expenses) {
   if (expense.getCreditOrDebit().equalsIgnoreCase("CREDIT")) {
    totalIncome += expense.getAmount();
   } else if (expense.getCreditOrDebit().equalsIgnoreCase("DEBIT")) {
    totalExpenses += expense.getAmount();
   }
  }

  dataset.addValue(totalIncome, "Income", "");
  dataset.addValue(totalExpenses, "Expenses", "");

  return dataset;
 }

 public String[] getCreditOrDebitArray() {
  List<String> creditOrDebitList = expenseRepository.findAll()
          .stream()
          .map(ExpenseEntity::getCreditOrDebit)
          .collect(Collectors.toList());

  return creditOrDebitList.toArray(new String[0]);
 }

 public int[] getAmountsArray() {
  List<Integer> amountsList = expenseRepository.findAllAmounts();
  return amountsList.stream().mapToInt(Integer::intValue).toArray();
 }

 public int getTotalCredits() {
  return expenseRepository.calculateTotalCredits();
 }

 public int getTotalDebits() {
  return expenseRepository.calculateTotalDebits();
 }


 /////////////////////////////// CHARTS

 // CHART MONTHLY BALANCES

 public Map<String, Double> getMonthlyTotals() {
  List<ExpenseEntity> allExpenses = expenseRepository.findAll();
  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  for (ExpenseEntity expense : allExpenses) {
   String month = expense.getDate().getMonth().toString();
   double amount = expense.getAmount();

   monthlyTotals.merge(month, amount, Double::sum);
  }

  return monthlyTotals;
 }

 public Map<String, Double> getMonthlyCreditsForYear(int year) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<ExpenseEntity> allExpenses = expenseRepository.findByDateBetween(startDate, endDate);
  Map<String, Double> monthlyCredits = new LinkedHashMap<>();
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

  for (ExpenseEntity expense : allExpenses) {
   String monthYear = expense.getDate().format(formatter);
   double amount = expense.getAmount();
   String creditOrDebit = expense.getCreditOrDebit();

   if ("credit".equalsIgnoreCase(creditOrDebit)) {
    monthlyCredits.merge(monthYear, amount, Double::sum);
   }
  }

  return monthlyCredits;
 }

 public Map<String, Double> getMonthlyDebitsForYear(int year) {
  LocalDate startDate = YearMonth.of(year, 1).atDay(1);
  LocalDate endDate = YearMonth.of(year, 12).atEndOfMonth();

  List<ExpenseEntity> allExpenses = expenseRepository.findByDateBetween(startDate, endDate);
  Map<String, Double> monthlyDebits = new LinkedHashMap<>();
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

  for (ExpenseEntity expense : allExpenses) {
   String monthYear = expense.getDate().format(formatter);
   double amount = expense.getAmount();
   String creditOrDebit = expense.getCreditOrDebit();

   if ("debit".equalsIgnoreCase(creditOrDebit)) {
    monthlyDebits.merge(monthYear, amount, Double::sum);
   }
  }

  return monthlyDebits;
 }



 // CHART MONTHLY INCOME  WITH CATEGORIES

 public Map<String, Double> getMonthlyCreditsByCategory(YearMonth yearMonth) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  List<CategoryEntity> categories = categoryRepository.findAll();
  Map<String, Double> monthlyCredits = new LinkedHashMap<>();

  if (!categories.isEmpty()) {
   for (CategoryEntity category : categories) {
    List<ExpenseEntity> expenses = expenseRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
    double total = expenses.stream()
            .filter(expense -> "credit".equalsIgnoreCase(expense.getCreditOrDebit()))
            .mapToDouble(ExpenseEntity::getAmount)
            .sum();

    monthlyCredits.put(category.getCategory(), total);
   }
  }

  return monthlyCredits;
 }

 // CHART MONTHLY EXPENSES  WITH CATEGORIES



 public Map<String, Double> getMonthlyDebitsByCategory(YearMonth yearMonth) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  List<CategoryEntity> categories = categoryRepository.findAll();
  Map<String, Double> monthlyDebits = new LinkedHashMap<>();

  if (!categories.isEmpty()) {
   for (CategoryEntity category : categories) {
    List<ExpenseEntity> expenses = expenseRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
    double total = expenses.stream()
            .filter(expense -> "debit".equalsIgnoreCase(expense.getCreditOrDebit()))
            .mapToDouble(ExpenseEntity::getAmount)
            .sum();

    monthlyDebits.put(category.getCategory(), total);
   }
  }

  return monthlyDebits;
 }

// CATEGORIES FILTERING

 public Map<String, Double> getMonthlyCreditsByCategory(YearMonth yearMonth, CategoryEntity categoryEntity) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  Map<String, Double> monthlyCredits = new LinkedHashMap<>();

  List<ExpenseEntity> expenses = expenseRepository.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  double total = expenses.stream()
          .filter(expense -> "credit".equalsIgnoreCase(expense.getCreditOrDebit()))
          .mapToDouble(ExpenseEntity::getAmount)
          .sum();

  monthlyCredits.put(categoryEntity.getCategory(), total);

  return monthlyCredits;
 }

 public Map<String, Double> getMonthlyDebitsByCategory(YearMonth yearMonth, CategoryEntity categoryEntity) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  Map<String, Double> monthlyDebits = new LinkedHashMap<>();

  List<ExpenseEntity> expenses = expenseRepository.findAllByDateBetweenAndCategory(startDate, endDate, categoryEntity);
  double total = expenses.stream()
          .filter(expense -> "debit".equalsIgnoreCase(expense.getCreditOrDebit()))
          .mapToDouble(ExpenseEntity::getAmount)
          .sum();

  monthlyDebits.put(categoryEntity.getCategory(), total);

  return monthlyDebits;
 }


}
