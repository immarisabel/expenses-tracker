package nl.marisabel.backend.transactions.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
public class ManualTransactionController {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public ManualTransactionController(TransactionRepository transactionRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions/edit/{id}")
    public String showEditTransactionForm(@PathVariable("id") Long id, Model model) {
        TransactionEntity transaction = transactionService.getTransaction(id);
        log.info(transaction.getEntity());
        model.addAttribute("transaction", transaction);
        return "transactions/manual-transaction";
    }

    @PostMapping("/transactions/edit")
    public String editTransaction(@ModelAttribute("transaction") TransactionEntity transaction, RedirectAttributes redirectAttributes) {
        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        log.info(transaction.getDate());
        redirectAttributes.addFlashAttribute("message", "Transaction edited successfully. ID: " + savedTransaction.getId());
        return "redirect:/transactions";
    }
}
