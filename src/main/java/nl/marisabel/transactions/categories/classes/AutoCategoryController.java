package nl.marisabel.transactions.categories.classes;

import lombok.extern.log4j.Log4j2;
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

 private final AutoCategoryServiceImp autoCategoryService;
 private final CategoryServiceImp categoryService;
 private String message;

 public AutoCategoryController(AutoCategoryServiceImp autoCategoryService, CategoryServiceImp categoryService) {
  this.autoCategoryService = autoCategoryService;
  this.categoryService = categoryService;
 }


 /**
  * Load the form on /auto-category
  * @param model auto-category.html
  * */
 @GetMapping
 public String getAutoCategoryForm(Model model) {
  model.addAttribute("autoCategoryList", autoCategoryService.getAutoCategoriesList());
  model.addAttribute("autoCategoryEntity", new AutoCategoryEntity());
  model.addAttribute("message", message);
  return "categories/auto-category";
 }

 /**
  * saves queries as list in the database along with the category matching
  * @param queries
  * @return redirect:/auto-category
  */
 @PostMapping
 public String saveAutoCategory(AutoCategoryEntity autoCategoryEntity, @RequestParam String queries) {
  List<String> queriesList = Arrays.stream(queries.split(","))
          .map(String::trim)
          .collect(Collectors.toList());
  autoCategoryEntity.setQueries(queriesList);
  autoCategoryService.saveAutocategory(autoCategoryEntity);
  return "redirect:/auto-category";
 }

 /**
  * GET: Load the data of the given category query (id) on the form in order to edit
  * POST: Updates the existing category
  * @param id id of the autoCategory to be updated
  * @return redirect:/auto-category
  */
 @GetMapping("/update")
 public String showFormForUpdatingAutoCategory(@RequestParam("id") Long id, Model model) {
  model.addAttribute("autoCategoryEntity", autoCategoryService.getAutocategoryToEdit(id));
  model.addAttribute("autoCategoryList", autoCategoryService.getAutoCategoriesList());
  return "categories/auto-category";
 }

 @PostMapping("/update")
 public String updateAutoCategory(@ModelAttribute("autoCategoryEntity") AutoCategoryEntity autoCategoryEntity, Model model) {
  autoCategoryService.saveAutocategory(autoCategoryEntity);
  model.addAttribute("message", "Category updated.");
  return "redirect:/auto-category";
 }

 /**
  * Automatically categorizes transactions based on predefined queries.
  * Loads uncategorized transactions and autocategory queries, then matches the queries to the
  * transaction descriptions or entities and assigns the corresponding category.
  * If a category does not exist, it creates a new one.
  *
  * @param model
  * @return redirect:/auto-category
  */
 @PostMapping("/autoCategorize")
 public String autoCategorize(Model model) {

  int numCategorized = categoryService.autoCategorizeTransactions();

  message = "Transactions categorized: "+ numCategorized;
  model.addAttribute("message", message);
  log.info(message);
  return "redirect:/auto-category";
 }


}
