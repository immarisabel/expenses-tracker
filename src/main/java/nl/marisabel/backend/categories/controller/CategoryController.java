package nl.marisabel.backend.categories.controller;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String showCategoryPage(Model model) {
        List<CategoryEntity> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new CategoryEntity());
        return "category-page";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("newCategory") CategoryEntity newCategory) {
        categoryRepository.save(newCategory);
        return "redirect:/categories";
    }


    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));
        for (ExpenseEntity expense : category.getExpenses()) {
            expense.getCategories().remove(category);
        }
        categoryRepository.delete(category);
        return "redirect:/categories";
    }


}
