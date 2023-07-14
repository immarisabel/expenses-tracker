package nl.marisabel.transactions.categories.classes;

import nl.marisabel.transactions.classes.TransactionEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

 private final CategoryServiceImp categoryServiceImp;

 public CategoryController(CategoryServiceImp categoryServiceImp) {
  this.categoryServiceImp = categoryServiceImp;
 }

 /**
  * SHOW CATEGORY PAGE
  * display form to create or edit new categories, list of categories and auto-category option
  * @return .../categories
  */
 @GetMapping
 public String showCategoryPage(Model model) {
  model.addAttribute("categories", categoryServiceImp.getCategories());
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
  categoryServiceImp.saveOrUpdate(newCategory);
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
  model.addAttribute("category", categoryServiceImp.getCategory(id));
  model.addAttribute("categories", categoryServiceImp.getCategories());
  return "categories/category-page";
 }

 /**
  * UPDATE CATEGORY
  * @return POST .../categories/update?id=LONG
  */
 @PostMapping("/update")
 public String updateCategory(@ModelAttribute("category") CategoryEntity category) {
  categoryServiceImp.saveOrUpdate(category);
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
  CategoryEntity categoryEntity = categoryServiceImp.getCategory(id);
  categoryEntity.setCategory("");
  categoryServiceImp.saveOrUpdate(categoryEntity);
  categoryServiceImp.deleteCategory(id);
  return "redirect:/categories";
 }
}
