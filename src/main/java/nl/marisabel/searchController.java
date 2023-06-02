package nl.marisabel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
class SearchController {

 private final ExpenseRepository expenseRepository;

 public SearchController(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
 }

 @GetMapping("/search")
 public String searchExpenses(@RequestParam("keyword") String keyword, Model model) {
  List<ExpensesModel> expenses = expenseRepository.findByDescriptionContainingIgnoreCaseOrEntityContainingIgnoreCase(keyword, keyword);

  if (expenses.isEmpty()) {
   model.addAttribute("message", "No data found for '" + keyword + "'.");
  } else {
   model.addAttribute("expenses", expenses);
  }

  return "search-results";
 }
}
