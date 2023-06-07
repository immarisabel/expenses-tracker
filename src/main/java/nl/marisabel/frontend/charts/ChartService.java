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

 public DefaultCategoryDataset generateExpenseIncomeDataset() {
  List<ExpenseEntity> expenses = expenseRepository.findAll(); // Replace with your actual query

  // Create a dataset for the chart
  DefaultCategoryDataset dataset = new DefaultCategoryDataset();

  // Calculate total income and expenses
  double totalIncome = 0;
  double totalExpenses = 0;

  for (ExpenseEntity expense : expenses) {
   if (expense.getCreditOrDebit().equalsIgnoreCase("CREDIT")) {
    totalIncome += expense.getAmount();
   } else if (expense.getCreditOrDebit().equalsIgnoreCase("DEBIT")) {
    totalExpenses += expense.getAmount();
   }
  }

  // Add total income and expenses to the dataset
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


 public Map<String, Double> getMonthlyTotals() {
  List<ExpenseEntity> allExpenses = expenseRepository.findAll();

  Map<String, Double> monthlyTotals = new LinkedHashMap<>();

  // Calculate totals for each month
  for (ExpenseEntity expense : allExpenses) {
   String month = expense.getDate().getMonth().toString(); // Get the month (e.g., "JANUARY")
   double amount = expense.getAmount();

   monthlyTotals.merge(month, amount, Double::sum);
  }

  return monthlyTotals;
 }

 public Map<String, Double> getMonthlyCredits() {
  List<ExpenseEntity> allExpenses = expenseRepository.findAll();

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

 public Map<String, Double> getMonthlyDebits() {
  List<ExpenseEntity> allExpenses = expenseRepository.findAll();

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


 // CATEGORY FILTER


 public Map<String, Double> getMonthlyCreditsByCategory(YearMonth yearMonth) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  List<CategoryEntity> categories = categoryRepository.findAll();  // Assuming you have a repository for categories

  Map<String, Double> monthlyCredits = new LinkedHashMap<>();

  if (!categories.isEmpty()) {
   for (CategoryEntity category : categories) {
    List<ExpenseEntity> expenses = expenseRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
    double total = 0.0;

    for (ExpenseEntity expense : expenses) {
     if ("credit".equalsIgnoreCase(expense.getCreditOrDebit())) {
      total += expense.getAmount();
     }
    }

    monthlyCredits.put(category.getCategory(), total);
   }
  }

  return monthlyCredits;
 }

 public Map<String, Double> getMonthlyDebitsByCategory(YearMonth yearMonth) {
  LocalDate startDate = yearMonth.atDay(1);
  LocalDate endDate = yearMonth.atEndOfMonth();

  List<CategoryEntity> categories = categoryRepository.findAll();  // Assuming you have a repository for categories

  Map<String, Double> monthlyDebits = new LinkedHashMap<>();

  if (!categories.isEmpty()) {
   for (CategoryEntity category : categories) {
    List<ExpenseEntity> expenses = expenseRepository.findAllByDateBetweenAndCategory(startDate, endDate, category);
    double total = 0.0;

    for (ExpenseEntity expense : expenses) {
     if ("debit".equalsIgnoreCase(expense.getCreditOrDebit())) {
      total += expense.getAmount();
     }
    }

    monthlyDebits.put(category.getCategory(), total);
   }
  }

  return monthlyDebits;
 }



}
