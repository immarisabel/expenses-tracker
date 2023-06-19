package nl.marisabel.backend.categories.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.AutoCategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequestMapping("/auto-category")
public class AutoCategoryController {

    private final AutoCategoryRepository autoCategoryRepository;
    private final TransactionRepository transactionRepository;

    public AutoCategoryController(AutoCategoryRepository autoCategoryRepository, TransactionRepository transactionRepository) {
        this.autoCategoryRepository = autoCategoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public String getAutoCategoryForm(Model model) {
        model.addAttribute("autoCategoryList", autoCategoryRepository.findAll());
        model.addAttribute("autoCategoryEntity", new AutoCategoryEntity());
        return "categories/auto-category";
    }

    @PostMapping
    public String saveAutoCategory(AutoCategoryEntity autoCategoryEntity, @RequestParam String queries) {
        List<String> queriesList = Arrays.stream(queries.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        autoCategoryEntity.setQueries(queriesList);
        autoCategoryRepository.save(autoCategoryEntity);
        return "redirect:/auto-category";
    }

    @GetMapping("/auto-category-list")
    public String getAutoCategoryList(Model model) {
        model.addAttribute("autoCategoryList", autoCategoryRepository.findAll());
        return "categories/auto-category-table";
    }

    @GetMapping("/auto-category-search")
    public String searchAutoCategory(@RequestParam String query, Model model) {
        model.addAttribute("autoCategoryList", autoCategoryRepository.findByQuery(query));
        return "categories/auto-category-table";
    }


    @GetMapping("/update")
    public String showFormForUpdatingAutoCategory(@RequestParam("id") Long id, Model model) {
        log.info("Getting category " + id + " to update");
        model.addAttribute("autoCategoryEntity", autoCategoryRepository.getReferenceById(id));
        model.addAttribute("autoCategoryList", autoCategoryRepository.findAll());
        return "categories/auto-category";
    }

    @PostMapping("/update")
    public String updateAutoCategory(@ModelAttribute("autoCategoryEntity") AutoCategoryEntity autoCategoryEntity) {
        autoCategoryRepository.save(autoCategoryEntity);
        log.info("Category updated.");
        return "redirect:/auto-category";
    }


    @PostMapping("/autoCategorize")
    public String autoCategorize(Model model) {
        List<TransactionEntity> unCategorizedTransactions = transactionRepository.findByCategoriesEmpty();
        List<AutoCategoryEntity> autoCategories = autoCategoryRepository.findAll();

        int numCategorized = 0;

        for (TransactionEntity transaction : unCategorizedTransactions) {
            for (AutoCategoryEntity autoCategory : autoCategories) {
                for (String query : autoCategory.getQueries()) {
                    if (transaction.getEntity().toLowerCase().contains(query.toLowerCase()) ||
                            transaction.getDescription().toLowerCase().contains(query.toLowerCase())) {
                        CategoryEntity newCategory = new CategoryEntity();
                        newCategory.setCategory(autoCategory.getCategory());

                        transaction.addCategory(newCategory);
                        transactionRepository.save(transaction);

                        numCategorized++;
                        break;

                    }
                }
            }
        }
        model.addAttribute("message", "Auto-categorized " + numCategorized + " transactions");
        return "redirect:/auto-category";
    }
}
