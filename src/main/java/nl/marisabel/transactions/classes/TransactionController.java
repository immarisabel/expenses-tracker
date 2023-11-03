package nl.marisabel.transactions.classes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.classes.CategoryEntity;
import nl.marisabel.transactions.categories.interfaces.CategoryRepository;
import nl.marisabel.transactions.categories.classes.CategoryServiceImp;
import nl.marisabel.transactions.interfaces.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@Log4j2
@RequestMapping("/transactions")
public class TransactionController {
 @Autowired
 private HttpServletRequest request;
 private final TransactionRepository transactionRepository;
 private final TransactionServiceImp transactionService;
 private final CategoryServiceImp categoryService;

 public TransactionController(
         TransactionRepository transactionRepository,
         CategoryRepository categoryRepository,
         TransactionServiceImp transactionService,
         CategoryServiceImp categoryService) {
  this.transactionRepository = transactionRepository;
  this.transactionService = transactionService;
  this.categoryService = categoryService;
 }

 //..........UPDATE CATEGORY IN BATCH
 @PostMapping("/updateCategory")
 public String batchUpdateCategory(
         @RequestParam("categoryId") Long categoryId,
         @RequestParam(value = "selectedTransactionsIds", required = false) String[] selectedTransactionsIds,
         @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
         @RequestParam(value = "pageSize", required = false, defaultValue = "100") int pageSize,
         RedirectAttributes redirectAttributes,
         HttpServletRequest request
 ) {
  if (selectedTransactionsIds == null) {
   redirectAttributes.addFlashAttribute("error", "No transactions selected");
   log.info("No transactions selected");
   return "redirect:/transactions";
  }
  List<Long> transactionsId = Arrays.stream(selectedTransactionsIds)
          .map(Long::valueOf)
          .collect(Collectors.toList());
  log.info("....transactions to update: " + transactionsId.size());

  try {
   categoryService.batchUpdateCategory(categoryId, transactionsId);
   log.info("Transaction updated successfully");
   redirectAttributes.addFlashAttribute("message", "Transaction updated successfully");
  } catch (Exception e) {
   log.info("Error updating transaction: " + e.getMessage());
   redirectAttributes.addFlashAttribute("error", "Error updating transaction: " + e.getMessage());
  }

  String previousUrl = request.getHeader("Referer");
  return "redirect:" + previousUrl;
 }


 @PostMapping("/delete-transaction")
 public String deletesTransaction(@RequestParam("id") Long id, Model model) {
  Optional<TransactionEntity> transactionOptional = transactionRepository.findById(id);
  if (transactionOptional.isPresent()) {
   TransactionEntity transaction = transactionOptional.get();
   transaction.removeCategories();
   transactionRepository.save(transaction);
   transactionRepository.deleteById(id);
   log.info(".... deleted transaction " + id);
  }
  return "redirect:/transactions";
 }


 /**
  * EDIT TRANSACTIONS
  */

 @GetMapping("/edit/{id}")
 public String showEditTransactionForm(@PathVariable("id") Long id, Model model) {

  TransactionEntity transaction = transactionService.getTransaction(id);
  List<CategoryEntity> categories = categoryService.getCategories();

  model.addAttribute("categories", categories);
  model.addAttribute("transaction", transaction);

  log.info("Transaction to be edited: " + transaction);

  return "transactions/manual-transaction";
 }



 @PostMapping("/edit")
 public String editTransaction(@ModelAttribute("transaction") TransactionEntity transaction,
                               @ModelAttribute("category") CategoryEntity category,
                               RedirectAttributes redirectAttributes) {
  log.info("Transaction edited: " + transaction);


  if (transaction.getCategories().equals(transaction.getCategories())) {
   transaction.setCategories(transaction.getCategories());
  }

  TransactionEntity savedTransaction = transactionRepository.save(transaction);

  log.info("Category added: " + transaction.getCategories().toString());

  redirectAttributes.addFlashAttribute("message", "Transaction edited successfully. ID: " + savedTransaction.getId());
  return "redirect:/transactions";
 }

}
