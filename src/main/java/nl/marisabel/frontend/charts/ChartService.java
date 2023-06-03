package nl.marisabel.frontend.charts;

import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
}
