package nl.marisabel.backend.categories.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String showCategoryPage(Model model) {
        List<CategoryEntity> categories = categoryService.getCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new CategoryEntity());
        model.addAttribute("category", new CategoryEntity()); // Add this line
        return "category-page";
    }


    @PostMapping("/add")
    public String addCategory(@ModelAttribute("newCategory") CategoryEntity newCategory) {
        categoryService.saveOrUpdate(newCategory);
        return "redirect:/categories";
    }

    @GetMapping("/update")
    public String showFormForUpdatingCategory(@RequestParam("id") Long id, Model model) {
        CategoryEntity category = categoryService.getCategory(id);
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("newCategory", new CategoryEntity());
        model.addAttribute("category", category);
        log.info(".... updated "+ category.getId() + category.getCategory());
        return "category-page";
    }


    @PostMapping("/update")
    public String updateCategory(@ModelAttribute("category") CategoryEntity category) {
        categoryService.saveOrUpdate(category);
        log.info(".... updating");
        return "redirect:/categories";
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("id") Long id, Model model, ExpenseEntity expense) {
        CategoryEntity categoryEntity = categoryService.getCategory(id);
        categoryEntity.setCategory("");
        categoryService.saveOrUpdate(categoryEntity);
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
