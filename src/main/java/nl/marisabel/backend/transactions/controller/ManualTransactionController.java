package nl.marisabel.backend.transactions.controller;

import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ManualTransactionController {
    private final TransactionRepository transactionRepository;



    public ManualTransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/transactions/new")
    public String showAddTransactionForm(Model model) {
        model.addAttribute("transaction", new TransactionEntity());
        return "transactions/manual-transaction";
    }

    @PostMapping("/transactions")
    public String addTransaction(@ModelAttribute("transaction") TransactionEntity transaction, RedirectAttributes redirectAttributes) {
        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        redirectAttributes.addFlashAttribute("message", "Transaction added successfully. ID: " + savedTransaction.getId());
        return "redirect:/transactions";
    }
}
