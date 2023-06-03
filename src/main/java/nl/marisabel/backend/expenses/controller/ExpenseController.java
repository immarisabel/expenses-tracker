package nl.marisabel.backend.expenses.controller;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import nl.marisabel.backend.expenses.entity.ExpenseFormDto;
import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import nl.marisabel.util.ExpensesCvsReaderING;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ExpenseController {

 private final ExpensesCvsReaderING expensesCvsReader;
 private final ExpenseRepository expenseRepository;
 private  final CategoryRepository categoryRepository;

 public ExpenseController(ExpensesCvsReaderING expensesCvsReader, ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
  this.expensesCvsReader = expensesCvsReader;
  this.expenseRepository = expenseRepository;
  this.categoryRepository = categoryRepository;
 }

 @GetMapping("/upload")
 public String showUploadForm() {
  return "upload";
 }

 @PostMapping("/upload")
 public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
  if (file.isEmpty()) {
   model.addAttribute("message", "Please select a file to upload.");
   return "upload";
  }

  try {
   expensesCvsReader.read(file.getInputStream());
   model.addAttribute("message", "File uploaded successfully.");
  } catch (Exception e) {
   model.addAttribute("message", "Error uploading file: " + e.getMessage());
  }

  return "upload";
 }

 @GetMapping("/expenses")
 public String showExpenses(Model model) {
  List<ExpenseEntity> expenses = expenseRepository.findAll();
  List<CategoryEntity> categories = categoryRepository.findAll();
  model.addAttribute("expenses", expenses);
  model.addAttribute("categories", categories);
  model.addAttribute("expenseForm", new ExpenseFormDto());
  return "expenses";
 }


 @PostMapping("/expenses/updateCategory")
 public String updateCategory(@RequestParam("selectedExpenseIds") List<Long> selectedExpenseIds,
                              @RequestParam("categoryId") Long categoryId) {
  CategoryEntity category = categoryRepository.findById(Math.toIntExact(categoryId)).orElse(null);
  if (category != null) {
   List<ExpenseEntity> expenses = expenseRepository.findAllById(selectedExpenseIds);
   for (ExpenseEntity expense : expenses) {
    expense.getCategories().add(category);
   }
   expenseRepository.saveAll(expenses);
  }
  return "redirect:/expenses";
 }
}
