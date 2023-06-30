package nl.marisabel.frontend.transactions;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.entity.TransactionForm;
import nl.marisabel.backend.transactions.model.TransactionFilter;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@Log4j2
public class TransactionsAndFiltersController {


 private final TransactionService transactionService;
 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 public TransactionsAndFiltersController(TransactionService transactionService,
                                         TransactionRepository transactionRepository,
                                         CategoryRepository categoryRepository) {
  this.transactionService = transactionService;
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }


 // .......... ADV ILTER
 @GetMapping("/transactions/filters")
 public String showFilterForm(Model model) {
  model.addAttribute("filter", new TransactionFilter());
  return "transactions/filter-form";
 }

 @PostMapping("/transactions/filters")
 public String filterTransactions(@ModelAttribute TransactionFilter filter, @RequestParam(defaultValue = "0") int page, Model model) {
  int size = 20;
  Pageable pageable = transactionService.createPageable("date", page, size);
  model.addAttribute("transactions", transactionService.filterTransactions(filter, pageable));
  return "transactions/filtered-page";
 }


 //.......... D E F A U L T
 @GetMapping("/transactions")
 public String showTransactions(@RequestParam(value = "sort", defaultValue = "date") String sort, Pageable pageable, @RequestParam(defaultValue = "0") int page, Model model) {
  int size = 20;
   pageable = transactionService.createPageable(sort, page, size);

  model.addAttribute("transactions", transactionRepository.findAll(pageable));
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("transactionsForm", new TransactionForm());
  model.addAttribute("sort", sort);
  return "transactions/transactions";
 }



 //.......... CATEGORY FILTER
 @GetMapping("/transactions/categories")
 public String showCategoryCharts(@RequestParam("categoryId") Long categoryId, @RequestParam(defaultValue = "0") int page, Model model) {
  int size = 50;
  PageRequest pageRequest = PageRequest.of(page, size);
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("transactions", transactionService.getTransactionsByCategory(categoryId, pageRequest));
  model.addAttribute("categoryId", categoryId);

  return "transactions/filtered-page";
 }


 //.......... NO CATEGORY FILTER
 @GetMapping("/transactions/categories/none")
 public String showTransactionsWithoutCategory(@RequestParam(defaultValue = "0") int page, Model model) {
  int size = 50;

  model.addAttribute("transactions", transactionRepository.findByCategoriesIsEmpty(PageRequest.of(page, size)));
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("currentPage", page);
  model.addAttribute("hideMe", "hide");

  return "transactions/filtered-page";
 }


}
