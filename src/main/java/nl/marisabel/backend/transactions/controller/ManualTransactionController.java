package nl.marisabel.backend.transactions.controller;

import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import nl.marisabel.backend.transactions.repository.ExpenseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ManualTransactionController {
    private final ExpenseRepository expenseRepository;



    public ManualTransactionController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/transactions/new")
    public String showAddTransactionForm(Model model) {
        model.addAttribute("transaction", new ExpenseEntity());
        return "manual-transaction";
    }

    @PostMapping("/transactions")
    public String addTransaction(@ModelAttribute("transaction") ExpenseEntity transaction, RedirectAttributes redirectAttributes) {
        ExpenseEntity savedTransaction = expenseRepository.save(transaction);
        redirectAttributes.addFlashAttribute("message", "Transaction added successfully. ID: " + savedTransaction.getId());
        return "redirect:/expenses";
    }
}
