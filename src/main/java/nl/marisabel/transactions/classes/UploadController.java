package nl.marisabel.transactions.classes;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.utils.upload.TransactionUploadResult;
import nl.marisabel.utils.upload.TransactionsCSVReaderING;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
public class UploadController {

 private final TransactionsCSVReaderING transactionsCVSReader;

 public UploadController(TransactionsCSVReaderING transactionsCVSReader) {
  this.transactionsCVSReader = transactionsCVSReader;
 }

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



}
