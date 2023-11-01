package nl.marisabel.transactions.classes;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.interfaces.CategoryRepository;
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


 private final TransactionServiceImp transactionServiceImp;
 private final CategoryRepository categoryRepository;

 public FiltersController(TransactionServiceImp transactionServiceImp, CategoryRepository categoryRepository) {
  this.transactionServiceImp = transactionServiceImp;
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
// @GetMapping("/transactions/filtered")
// public String showAdvancedFilteredTransactions(TransactionFilter filter,
//                                                @RequestParam(defaultValue = "0") int page,
//                                                Model model) {
//  Pageable pageable = transactionServiceImp.createPageable("date", page, 20);
//  model.addAttribute("filter", filter);
//  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
//  model.addAttribute("transactions", transactionServiceImp.getFilteredTransactions(filter));
//  return "transactions/filtered-page";
// }


 @GetMapping("/transactions/filtered")
 public String showAdvancedFilteredTransactions(TransactionFilter filter,
                                                @RequestParam(defaultValue = "0") int page,
                                                Model model) {
  Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Order.asc("date")));
  Page<TransactionEntity> filteredTransactions = transactionServiceImp.filterTransactions(filter, pageable);

  model.addAttribute("filter", filter);
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("transactions", filteredTransactions);

  return "transactions/filtered-page";
 }



 @PostMapping("/transactions/filtered")
 public String filterAdvancedTransactions(@ModelAttribute TransactionFilter filter,
                                          Pageable pageable,
                                          @RequestParam(defaultValue = "0") int page,
                                          Model model) {

  int size = 20;
  pageable = transactionServiceImp.createPageable("date", page, size);
  Page<TransactionEntity> filteredTransactions = transactionServiceImp.filterTransactions(filter, pageable);
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
   pageable = transactionServiceImp.createPageable(sort, page, size);

  model.addAttribute("transactions", transactionServiceImp.findAllPageable(pageable));
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
  model.addAttribute("transactions", transactionServiceImp.getTransactionsByCategory(categoryId, pageRequest));
  model.addAttribute("categoryId", categoryId);

  return "transactions/filtered-page";
 }


 //.......... NO CATEGORY FILTER
 @GetMapping("/transactions/categories/none")
 public String showTransactionsWithoutCategory(@RequestParam(defaultValue = "0") int page, Model model) {
  int size = 20;

  model.addAttribute("transactions", transactionServiceImp.findByCategoriesIsEmpty(PageRequest.of(page, size)));
  model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")));
  model.addAttribute("currentPage", page);
  model.addAttribute("hideMe", "hide");

  return "transactions/filtered-page";
 }


}
