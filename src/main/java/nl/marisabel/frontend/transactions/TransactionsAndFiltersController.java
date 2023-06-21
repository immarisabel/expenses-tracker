package nl.marisabel.frontend.transactions;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.entity.TransactionForm;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@Log4j2
public class TransactionsAndFiltersController {


 private final TransactionService transactionService;
 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 public TransactionsAndFiltersController(TransactionService transactionService, TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
  this.transactionService = transactionService;
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }


 //.......... D E F A U L T
 @GetMapping("/transactions")
 public String showTransactions(@RequestParam(defaultValue = "0") int page, Model model) {
  int size = 50;
  Page<TransactionEntity> transactions = transactionRepository.findAll(PageRequest.of(page, size));
  List<CategoryEntity> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category"));

  if (!transactions.isEmpty()) {
   log.info("ID of the first transaction object: " + transactions.get().findFirst().get().getId());
  } else {
   log.info("Transactions list is empty");
  }

  model.addAttribute("transactions", transactions);
  model.addAttribute("categories", categories);
  model.addAttribute("transactionsForm", new TransactionForm());
  return "transactions/transactions";
 }


 @GetMapping("/transactions/search")
 public String searchTransactions(
         @RequestParam(value = "searchTerm", required = false) String searchTerm,
         @RequestParam(defaultValue = "0") int page,
         Model model
 ) {
  int size = 50;
  PageRequest pageRequest = PageRequest.of(page, size);
  Page<TransactionEntity> searchResults = transactionService.searchTransactions(searchTerm, pageRequest);
  List<CategoryEntity> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category"));

  model.addAttribute("transactions", searchResults);
  model.addAttribute("categories", categories);
  model.addAttribute("searchTerm", searchTerm);
  model.addAttribute("message", searchResults.getTotalElements() + " transactions found");
  model.addAttribute("searchResults", searchResults);

  return "transactions/filtered-page";
 }


 //.......... DATE FILTERING
 @GetMapping("/transactions/filter")
 public String filterTransactionsByDate(
         @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
         @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
         @RequestParam(defaultValue = "0") int page,
         Model model) {

  int size = 50;
  PageRequest pageRequest = PageRequest.of(page, size);
  Page<TransactionEntity> filteredResults = transactionService.filterTransactionByDate(startDate, endDate, pageRequest);

  List<CategoryEntity> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category"));
  model.addAttribute("categories", categories);

  model.addAttribute("transactions", filteredResults);
  model.addAttribute("filteredCount", filteredResults.getTotalElements());
  model.addAttribute("currentPage", page);
  model.addAttribute("startDate", startDate);
  model.addAttribute("endDate", endDate);

  return "transactions/filtered-page";
 }


 //.......... CATEGORY FILTER
 @GetMapping("/transactions/categories")
 public String showCategoryCharts(@RequestParam("categoryId") Long categoryId, @RequestParam(defaultValue = "0") int page, Model model) {
  int size = 50;
  PageRequest pageRequest = PageRequest.of(page, size);
  Page<TransactionEntity> transactions = transactionService.getTransactionsByCategory(categoryId, pageRequest);
  List<CategoryEntity> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category"));

  model.addAttribute("categories", categories);
  model.addAttribute("transactions", transactions);
  model.addAttribute("categoryId", categoryId);

  return "transactions/filtered-page";
 }


 //.......... NO CATEGORY FILTER
 @GetMapping("/transactions/categories/none")
 public String showTransactionsWithoutCategory(@RequestParam(defaultValue = "0") int page, Model model) {
  int size = 50;  // Adjust this value to your desired page size
  Page<TransactionEntity> transactions = transactionRepository.findByCategoriesIsEmpty(PageRequest.of(page, size));
  List<CategoryEntity> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category"));

  model.addAttribute("transactions", transactions);
  model.addAttribute("categories", categories);
  model.addAttribute("currentPage", page);
  model.addAttribute("hideMe", "hide"); //for thymeleaf condition to hide if controller is not called
  return "transactions/filtered-page";
 }


}
