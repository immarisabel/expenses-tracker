package nl.marisabel.backend.transactions.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.upload.TransactionUploadResult;
import nl.marisabel.util.TransactionsCVSReaderING;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class TransactionController {

 private final TransactionsCVSReaderING transactionsCVSReader;
 private final TransactionRepository transactionRepository;
 private final TransactionService transactionService;

 public TransactionController(
         TransactionsCVSReaderING transactionsCVSReader,
         TransactionRepository transactionRepository,
         CategoryRepository categoryRepository,
         TransactionService transactionService
 ) {
  this.transactionsCVSReader = transactionsCVSReader;
  this.transactionRepository = transactionRepository;
  this.transactionService = transactionService;
 }



 //..........UPLOAD FILE

 @PostMapping("/upload")
 public String handleFileUpload(
         @RequestParam("file") MultipartFile file,
         RedirectAttributes redirectAttributes
 ) {
  String message = "";
  log.info(".... uploading " + file.getName());
  try {
   if (file.isEmpty()) {
    message = "Please select a file to upload.";
   } else {
    TransactionUploadResult result = transactionsCVSReader.read(file.getInputStream());
    redirectAttributes.addFlashAttribute("result", result);
    message = "File uploaded successfully.";

   }
  } catch (Exception e) {
   message = "Error uploading file: " + e.getMessage();

  }

  log.info("....  " + message);

  redirectAttributes.addFlashAttribute("message", message);
  return "redirect:/transactions";
 }


 //..........UPDATE CATEGORY IN BATCH
 @PostMapping("/transactions/updateCategory")
 public String batchUpdateCategory(
         @RequestParam("categoryId") Long categoryId,
         @RequestParam("selectedTransactionsIds") String[] selectedTransactionsIds,
         RedirectAttributes redirectAttributes,
         HttpServletRequest request
 ) {
  List<Long> transactionsId = Arrays.stream(selectedTransactionsIds)
          .map(Long::valueOf)
          .collect(Collectors.toList());
  log.info("....transaction to update: " + transactionsId.size());

  List<TransactionEntity> transaction = transactionRepository.findAllById(transactionsId);

  log.info(".... transaction updated: " + transaction);

  try {
   transactionService.batchUpdateCategory(categoryId, transaction);
   redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully");
  } catch (Exception e) {
   redirectAttributes.addFlashAttribute("errorMessage", "Error updating transaction: " + e.getMessage());
  }

  String redirectUrl = "redirect:/transactions/search";
  String queryString = request.getQueryString();
  if (queryString != null) {
   redirectUrl += "?" + queryString;
  }

  return redirectUrl;
 }



 //.......... CLEAR CATEGORY FROM TRANSACTION

  @PostMapping("/transactions/clearCategory")
 public String batchClearCategory(
         @RequestParam("categoryId") Long categoryId,
         @RequestParam("selectedTransactionsIds") String[] selectedTransactionsIds,
         RedirectAttributes redirectAttributes,
         HttpServletRequest request
 ) {
  List<Long> transactionsId = Arrays.stream(selectedTransactionsIds)
          .map(Long::valueOf)
          .collect(Collectors.toList());
  log.info("...transactions to update: " + transactionsId.size());

  List<TransactionEntity> transactions = transactionRepository.findAllById(transactionsId);

  log.info("...transactions updated: " + transactions);

  try {
   for (TransactionEntity transaction : transactions) {
    transaction.getCategories().clear(); // clear all existing categories
    transactionRepository.save(transaction);
   }
   redirectAttributes.addFlashAttribute("successMessage", "Categories cleared successfully");
  } catch (Exception e) {
   redirectAttributes.addFlashAttribute("errorMessage", "Error clearing categories: " + e.getMessage());
  }

  String redirectUrl = "redirect:/transactions/search";
  String queryString = request.getQueryString();
  if (queryString != null) {
   redirectUrl += "?" + queryString;
  }

  return redirectUrl;
 }


 //.......... DELETE TRANSACTION
 @PostMapping("/transactions/delete")
 public String deleteTransaction(@RequestParam("id") Long id, Model model) {
  transactionRepository.deleteById(id);
  log.info(".... deleting transaction #" + id);
  return "redirect:/transactions";
 }

}
