package nl.marisabel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ExpenseController {

 private final ExpensesCvsReaderING expensesCvsReader;
 private final ExpenseRepository expenseRepository;

 public ExpenseController(ExpensesCvsReaderING expensesCvsReader, ExpenseRepository expenseRepository) {
  this.expensesCvsReader = expensesCvsReader;
  this.expenseRepository = expenseRepository;
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
  List<ExpensesModel> expenses = expenseRepository.findAll();
  model.addAttribute("expenses", expenses);
  return "expenses";
 }
}
