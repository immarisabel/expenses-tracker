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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
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
    private final TransactionService transactionService;

    public TransactionController(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            TransactionService transactionService
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }


    //..........UPDATE CATEGORY IN BATCH
    @PostMapping("/updateCategory")
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

        try {
            transactionService.batchUpdateCategory(categoryId, transactionsId, pageNumber, pageSize);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating transaction: " + e.getMessage());
        }

        String previousUrl = request.getHeader("Referer");
        return "redirect:" + previousUrl;
    }


    @PostMapping("/delete-transaction")
    public String clearsCategory(@RequestParam("id") Long id, Model model) {
        Optional<TransactionEntity> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            TransactionEntity transaction = transactionOptional.get();
            transaction.removeCategories();
            transactionRepository.deleteById(id);
            log.info(".... deleted transaction " + id);
        }
        return "redirect:/transactions";
    }




}
