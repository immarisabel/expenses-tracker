package nl.marisabel.util;

import nl.marisabel.database.ExpenseRepository;
import nl.marisabel.database.ExpensesModel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartService {

 private final ExpenseRepository expenseRepository;

 public ChartService(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
 }

 public DefaultCategoryDataset generateExpenseIncomeDataset() {
  // Retrieve the expense and income data from the repository or data source
  List<ExpensesModel> expenses = expenseRepository.findAll(); // Replace with your actual query
  // Example: List<Income> incomes = incomeRepository.findAll();

  // Create a dataset for the chart
  DefaultCategoryDataset dataset = new DefaultCategoryDataset();

  // Add expense data to the dataset
  for (ExpensesModel expense : expenses) {
   // Assuming you have a date field and amount field in the Expense class
   dataset.addValue(expense.getAmount(), "Expenses", expense.getDate().toString());
  }

  // Add income data to the dataset
  // Example: for (Income income : incomes) {
  //                 dataset.addValue(income.getAmount(), "Income", income.getDate().toString());
  //             }

  return dataset;
 }
}
