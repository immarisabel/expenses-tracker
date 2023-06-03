package nl.marisabel.frontend.charts;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChartService {

 private final ExpenseRepository expenseRepository;

 public ChartService(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
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

  for (ExpenseEntity expense : allExpenses) {
   String month = expense.getDate().getMonth().toString();
   double amount = expense.getAmount();
   String creditOrDebit = expense.getCreditOrDebit();

   if ("credit".equalsIgnoreCase(creditOrDebit)) {
    monthlyCredits.merge(month, amount, Double::sum);
   }
  }

  return monthlyCredits;
 }

 public Map<String, Double> getMonthlyDebits() {
  List<ExpenseEntity> allExpenses = expenseRepository.findAll();

  Map<String, Double> monthlyDebits = new LinkedHashMap<>();

  for (ExpenseEntity expense : allExpenses) {
   String month = expense.getDate().getMonth().toString();
   double amount = expense.getAmount();
   String creditOrDebit = expense.getCreditOrDebit();

   if ("debit".equalsIgnoreCase(creditOrDebit)) {
    monthlyDebits.merge(month, amount, Double::sum);
   }
  }

  return monthlyDebits;
 }


}
