package nl.marisabel.controller;

import nl.marisabel.database.ExpenseRepository;
import nl.marisabel.database.ExpensesModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class ExpensesController {

 private final ExpenseRepository expenseRepository;

 public ExpensesController(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
 }

 @GetMapping("/expenses")
 public String showExpenses(Model model) {
  List<ExpensesModel> expenses = expenseRepository.findAll();
  model.addAttribute("expenses", expenses);
  return "expenses";
 }
}
