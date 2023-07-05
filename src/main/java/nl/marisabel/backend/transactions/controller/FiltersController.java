package nl.marisabel.backend.transactions.controller;

import lombok.extern.log4j.Log4j2;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Log4j2
public class FiltersController {


 private final TransactionService transactionService;
 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 public FiltersController(TransactionService transactionService,
                          TransactionRepository transactionRepository,
                          CategoryRepository categoryRepository) {
  this.transactionService = transactionService;
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }

//TODO make page: org.thymeleaf.exceptions.TemplateInputException:
// Error resolving template [transactions/no-results],
// template might not exist or might not be accessible by any of the configured Template Resolvers

 // .......... ADVANCED FILTER
 @GetMapping("/transactions/filters")
 public String showAdvancedFilterForm(Model model) {
  model.addAttribute("filter", new TransactionFilter());
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  return "transactions/filter-form";
 }
 // for now without  filters. Being an advance search, it should just give more limited transactions.
 @GetMapping("/transactions/filtered")
 public String showAdvancedFilteredTransactions(TransactionFilter filter,
                                                Model model) {
  model.addAttribute("filter", filter);
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("transactions", transactionService.getFilteredTransactions(filter));
  return "transactions/filtered-page";
 }


//
// @GetMapping("/transactions/filtered")
// public String showAdvancedFilteredTransactions(TransactionFilter filter,
//                                                @RequestParam(defaultValue = "0") int page,
//                                                Model model) {
//  int size = 20;
//  Pageable pageable = transactionService.createPageable("date", page, size);
//  model.addAttribute("filter", filter);
//  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
//  model.addAttribute("transactions", transactionService.getFilteredTransactions(filter, pageable));
//  return "transactions/filtered-page";
// }



 @PostMapping("/transactions/filtered")
 public String filterAdvancedTransactions(@ModelAttribute TransactionFilter filter,Pageable pageable, @RequestParam(defaultValue = "0") int page, Model model) {

  int size = 20;
  pageable = transactionService.createPageable("date", page, size);
  Page<TransactionEntity> filteredTransactions = transactionService.filterTransactions(filter, pageable);
  model.addAttribute("transactions", filteredTransactions);
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));

  if (filteredTransactions.isEmpty()) {
   return "transactions/no-results"; // Handle case when no results found
  } else {
   return "transactions/filtered-page"; // Load the filtered-page
  }
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
  int size = 20;
  PageRequest pageRequest = PageRequest.of(page, size);
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("transactions", transactionService.getTransactionsByCategory(categoryId, pageRequest));
  model.addAttribute("categoryId", categoryId);

  return "transactions/filtered-page";
 }


 //.......... NO CATEGORY FILTER
 @GetMapping("/transactions/categories/none")
 public String showTransactionsWithoutCategory(@RequestParam(defaultValue = "0") int page, Model model) {
  int size = 20;

  model.addAttribute("transactions", transactionRepository.findByCategoriesIsEmpty(PageRequest.of(page, size)));
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("currentPage", page);
  model.addAttribute("hideMe", "hide");

  return "transactions/filtered-page";
 }


}
