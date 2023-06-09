package nl.marisabel.backend.transactions.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import nl.marisabel.backend.transactions.entity.ExpenseFormDto;
import nl.marisabel.backend.transactions.repository.ExpenseRepository;
import nl.marisabel.backend.transactions.service.ExpenseService;
import nl.marisabel.frontend.upload.ExpenseUploadResult;
import nl.marisabel.util.ExpensesCvsReaderING;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class ExpenseController {

 private final ExpensesCvsReaderING expensesCvsReader;
 private final ExpenseRepository expenseRepository;
 private final CategoryRepository categoryRepository;
 private final ExpenseService expenseService;

 public ExpenseController(
         ExpensesCvsReaderING expensesCvsReader,
         ExpenseRepository expenseRepository,
         CategoryRepository categoryRepository,
         ExpenseService expenseService
 ) {
  this.expensesCvsReader = expensesCvsReader;
  this.expenseRepository = expenseRepository;
  this.categoryRepository = categoryRepository;
  this.expenseService = expenseService;
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
    ExpenseUploadResult result = expensesCvsReader.read(file.getInputStream());
    redirectAttributes.addFlashAttribute("result", result);
    message = "File uploaded successfully.";

   }
  } catch (Exception e) {
   message = "Error uploading file: " + e.getMessage();

  }

  log.info("....  " + message);

  redirectAttributes.addFlashAttribute("message", message);
  return "redirect:/expenses";
 }


 //..........UPDATE CATEGORY IN BATCH
 @PostMapping("/expenses/updateCategory")
 public String batchUpdateCategory(
         @RequestParam("categoryId") Long categoryId,
         @RequestParam("selectedExpenseIds") String[] selectedExpenseIds,
         RedirectAttributes redirectAttributes,
         HttpServletRequest request
 ) {
  List<Long> expenseIds = Arrays.stream(selectedExpenseIds)
          .map(Long::valueOf)
          .collect(Collectors.toList());
  log.info("....expenses to update: " + expenseIds.size());

  List<ExpenseEntity> expenses = expenseRepository.findAllById(expenseIds);

  log.info(".... expenses updated: " + expenses);

  try {
   expenseService.batchUpdateCategory(categoryId, expenses);
   redirectAttributes.addFlashAttribute("successMessage", "Expenses updated successfully");
  } catch (Exception e) {
   redirectAttributes.addFlashAttribute("errorMessage", "Error updating expenses: " + e.getMessage());
  }

  String redirectUrl = "redirect:/expenses/search";
  String queryString = request.getQueryString();
  if (queryString != null) {
   redirectUrl += "?" + queryString;
  }

  return redirectUrl;
 }



 //.......... CLEAR CATEGORY FROM TRANSACTION

  @PostMapping("/expenses/clearCategory")
 public String batchClearCategory(
         @RequestParam("categoryId") Long categoryId,
         @RequestParam("selectedExpenseIds") String[] selectedExpenseIds,
         RedirectAttributes redirectAttributes,
         HttpServletRequest request
 ) {
  List<Long> expenseIds = Arrays.stream(selectedExpenseIds)
          .map(Long::valueOf)
          .collect(Collectors.toList());
  log.info("...expenses to update: " + expenseIds.size());

  List<ExpenseEntity> expenses = expenseRepository.findAllById(expenseIds);

  log.info("...expenses updated: " + expenses);

  try {
   for (ExpenseEntity expense : expenses) {
    expense.getCategories().clear(); // clear all existing categories
    expenseRepository.save(expense);
   }
   redirectAttributes.addFlashAttribute("successMessage", "Categories cleared successfully");
  } catch (Exception e) {
   redirectAttributes.addFlashAttribute("errorMessage", "Error clearing categories: " + e.getMessage());
  }

  String redirectUrl = "redirect:/expenses/search";
  String queryString = request.getQueryString();
  if (queryString != null) {
   redirectUrl += "?" + queryString;
  }

  return redirectUrl;
 }


 //.......... DELETE EXPENSE
 @PostMapping("/expenses/delete")
 public String deleteExpenses(@RequestParam("id") Long id, Model model) {
  expenseRepository.deleteById(id);
  log.info(".... deleting transaction #" + id);
  return "redirect:/expenses";
 }

}
