package nl.marisabel.frontend;

import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

 private final ExpenseRepository expenseRepository;

 public SearchController(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
 }

 @GetMapping
 public String showSearchPage() {
  return "search";
 }

 @PostMapping
 public String searchExpenses(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
  if (keyword != null && !keyword.isEmpty()) {
   List<ExpenseEntity> expenses = expenseRepository.findByDescriptionContainingIgnoreCaseOrEntityContainingIgnoreCase(keyword, keyword);

   if (expenses.isEmpty()) {
    model.addAttribute("message", "No data found for '" + keyword + "'.");
   } else {
    model.addAttribute("expenses", expenses);
   }
  }

  return "search";
 }
}
