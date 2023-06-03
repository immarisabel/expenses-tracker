package nl.marisabel.frontend;

import nl.marisabel.util.ExpensesCvsReaderING;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/uploadForm")
public class UploadController {

 private final ExpensesCvsReaderING expensesCvsReader;

 public UploadController(ExpensesCvsReaderING expensesCvsReader) {
  this.expensesCvsReader = expensesCvsReader;
 }

 @GetMapping
 public String showUploadForm() {
  return "upload";
 }

 @PostMapping
 public String handleFileUpload(MultipartFile file, Model model) {
  try {
   expensesCvsReader.read(file.getInputStream());
   model.addAttribute("message", "File uploaded successfully!");
  } catch (Exception e) {
   model.addAttribute("error", "Error uploading file: " + e.getMessage());
  }
  return "upload";
 }
}

