package nl.marisabel.util;

import nl.marisabel.database.ExpenseRepository;
import nl.marisabel.database.ExpensesModel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ChartService {

 private final ExpenseRepository expenseRepository;

 public ChartService(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
 }

 public DefaultCategoryDataset generateExpenseIncomeDataset() {
  List<ExpensesModel> expenses = expenseRepository.findAll(); // Replace with your actual query

  // Create a dataset for the chart
  DefaultCategoryDataset dataset = new DefaultCategoryDataset();

  // Calculate total income and expenses
  double totalIncome = 0;
  double totalExpenses = 0;

  for (ExpensesModel expense : expenses) {
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

}
