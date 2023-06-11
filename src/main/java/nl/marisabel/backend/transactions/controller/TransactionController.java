package nl.marisabel.backend.transactions.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.service.TransactionService;
import nl.marisabel.frontend.upload.TransactionUploadResult;
import nl.marisabel.util.TransactionsCVSReaderING;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private HttpServletRequest request;
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
            @RequestParam(value = "selectedTransactionsIds", required = false) String[] selectedTransactionsIds,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {

        List<Long> transactionsId = Arrays.stream(selectedTransactionsIds)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        log.info("....transactions to update: " + transactionsId.size());

        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, transactionsId.size());
        List<Long> paginatedTransactionIds = transactionsId.subList(startIndex, endIndex);

        List<TransactionEntity> transactions = transactionRepository.findAllById(transactionsId);

        log.info("....transactions updated: " + transactions.size());

        try {
            transactionService.batchUpdateCategory(categoryId, transactions);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating transaction: " + e.getMessage());
        }

        String previousUrl = request.getHeader("Referer");
        return "redirect:" + previousUrl;

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

        int batchSize = 100; // Define the batch size as per your requirements
        int totalBatches = (int) Math.ceil((double) transactionsId.size() / batchSize);

        for (int batchNumber = 0; batchNumber < totalBatches; batchNumber++) {
            int startIndex = batchNumber * batchSize;
            int endIndex = Math.min(startIndex + batchSize, transactionsId.size());
            List<Long> batchIds = transactionsId.subList(startIndex, endIndex);

            List<TransactionEntity> transactions = transactionRepository.findAllById(batchIds);
            log.info("...transactions updated in batch " + (batchNumber + 1) + ": " + transactions);

            try {
                for (TransactionEntity transaction : transactions) {
                    transaction.getCategories().clear(); // clear all existing categories
                    transactionRepository.save(transaction);
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error clearing categories: " + e.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Categories cleared successfully");

        String previousUrl = request.getHeader("Referer");
        return "redirect:" + previousUrl;
    }


    //.......... DELETE TRANSACTION
    @PostMapping("/transactions/delete")
    public String deleteTransaction(@RequestParam("id") Long id, Model model) {
        transactionRepository.deleteById(id);
        log.info(".... deleting transaction #" + id);
        return "redirect:/transactions";
    }

}
