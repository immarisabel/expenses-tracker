package nl.marisabel.frontend.transactions;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import nl.marisabel.backend.transactions.entity.ExpenseFormDto;
import nl.marisabel.backend.transactions.repository.ExpenseRepository;
import nl.marisabel.backend.transactions.service.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@Log4j2
public class TransactionsAndFiltersControllers {


 private final ExpenseService expenseService;
 private final ExpenseRepository expenseRepository;
 private final CategoryRepository categoryRepository;

 public TransactionsAndFiltersControllers(ExpenseService expenseService, ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
  this.expenseService = expenseService;
  this.expenseRepository = expenseRepository;
  this.categoryRepository = categoryRepository;
 }


 //.......... D E F A U L T
 @GetMapping("/expenses")
 public String showExpenses(@RequestParam(defaultValue = "0") int page, Model model) {
  int size = 25; // or whatever number you want
  Page<ExpenseEntity> expenses = expenseRepository.findAll(PageRequest.of(page, size));
  List<CategoryEntity> categories = categoryRepository.findAll();
  model.addAttribute("expenses", expenses);
  model.addAttribute("categories", categories);
  model.addAttribute("expenseForm", new ExpenseFormDto());
  return "expenses";
 }



 //.......... KEYWORD SEARCH

 @GetMapping("/expenses/search")
 public String searchExpenses(
         @RequestParam(value = "searchTerm", required = false) String searchTerm,
         Model model
 ) {
  if (searchTerm != null) {
   log.info(".... SEARCHING FOR: " + searchTerm);
  }
  List<ExpenseEntity> searchResults = expenseRepository.findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm);
  List<CategoryEntity> categories = categoryRepository.findAll();
  model.addAttribute("expenses", searchResults);
  model.addAttribute("searchCount", searchResults.size());
  model.addAttribute("categories", categories);
  model.addAttribute("searchTerm", searchTerm);

  return "expenses";
 }


 //.......... DATE FILTERING

 @GetMapping("/expenses/filter")
 public String filterExpensesByDate(@RequestParam("startDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
                                    @RequestParam("endDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate,
                                    Model model) {
  List<ExpenseEntity> filteredResults = expenseService.filterExpensesByDate(startDate, endDate);
  model.addAttribute("expenses", filteredResults);
  model.addAttribute("filteredCount", filteredResults.size());
  model.addAttribute("currentPage", 0);
  return "expenses";
 }

}
