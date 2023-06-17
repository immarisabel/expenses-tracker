package nl.marisabel.backend.categories.controller;

import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.repository.AutoCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AutoCategoryController {

    @Autowired
    private AutoCategoryRepository autoCategoryRepository;

    @GetMapping("/auto-category")
    public String getAutoCategoryForm(Model model) {
        model.addAttribute("autoCategoryEntity", new AutoCategoryEntity());
        return "categories/auto-category";
    }

    @PostMapping("/auto-category")
    public String saveAutoCategory(AutoCategoryEntity autoCategoryEntity, @RequestParam String queries) {
        List<String> queriesList = Arrays.stream(queries.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        autoCategoryEntity.setQueries(queriesList);
        autoCategoryRepository.save(autoCategoryEntity);
        return "redirect:/auto-category-list";
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

}
