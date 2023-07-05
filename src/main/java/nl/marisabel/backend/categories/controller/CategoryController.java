package nl.marisabel.backend.categories.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.CategoryService;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

 private final CategoryService categoryService;

 public CategoryController(CategoryService categoryService) {
  this.categoryService = categoryService;
 }

 /**
  * SHOW CATEGORY PAGE
  * display form to create or edit new categories, list of categories and auto-category option
  * @return .../categories
  */
 @GetMapping
 public String showCategoryPage(Model model) {
  model.addAttribute("categories", categoryService.getCategories());
  model.addAttribute("newCategory", new CategoryEntity());
  model.addAttribute("category", new CategoryEntity());
  return "categories/category-page";
 }

 /**
  * ADD CATEGORY
  * create new category on POST from form
  * @param newCategory
  * @return .../categories/add = save new category
  */
 @PostMapping("/add")
 public String addCategory(@ModelAttribute("newCategory") CategoryEntity newCategory) {
  categoryService.saveOrUpdate(newCategory);
  return "redirect:/categories";
 }

 /**
  * SHOW FORM FOR UPDATING CATEGORY
  * Form to create new category is populated with teh category to be edited
  * @param id
  * @return GET .../categories/update?id=LONG
  */
 @GetMapping("/update")
 public String showFormForUpdatingCategory(@RequestParam("id") Long id, Model model) {
  model.addAttribute("category", categoryService.getCategory(id));
  model.addAttribute("categories", categoryService.getCategories());
  return "categories/category-page";
 }

 /**
  * UPDATE CATEGORY
  * @return POST .../categories/update?id=LONG
  */
 @PostMapping("/update")
 public String updateCategory(@ModelAttribute("category") CategoryEntity category) {
  categoryService.saveOrUpdate(category);
  return "redirect:/categories";
 }

 /**
  * DELETE CATEGORY
  * remove category from transactions
  * delete the catgeory
  * @param id
  * @param transaction
  * @return POST .../categories/delete
  * transactions remain - tested 5/7/23 17:31
  */
 @PostMapping("/delete")
 public String deleteCategory(@RequestParam("id") Long id, Model model, TransactionEntity transaction) {
  CategoryEntity categoryEntity = categoryService.getCategory(id);
  categoryEntity.setCategory("");
  categoryService.saveOrUpdate(categoryEntity);
  categoryService.deleteCategory(id);
  return "redirect:/categories";
 }
}
