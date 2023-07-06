package nl.marisabel.backend.categories.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.service.AutoCategoryServiceImp;
import nl.marisabel.backend.categories.service.CategoryServiceImp;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
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
 private final TransactionRepository transactionRepository;
 private final CategoryServiceImp categoryService;
 private String message;

 public AutoCategoryController(AutoCategoryServiceImp autoCategoryService, TransactionRepository transactionRepository, CategoryServiceImp categoryService) {
  this.autoCategoryService = autoCategoryService;
  this.transactionRepository = transactionRepository;
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
  List<TransactionEntity> unCategorizedTransactions = transactionRepository.findByCategoriesEmpty();
  log.info("Number of uncategorized transactions: " + unCategorizedTransactions.size());

  List<AutoCategoryEntity> autoCategories = autoCategoryService.getAutoCategoriesList();

  int numCategorized = 0;

  for (AutoCategoryEntity autoCategory : autoCategories) {
   // Try to find an existing CategoryEntity with this category
   CategoryEntity existingCategory = categoryService.findByCategory(autoCategory.getCategory());

   // If none exists, create a new one
   if (existingCategory == null) {
    existingCategory = new CategoryEntity();
    existingCategory.setCategory(autoCategory.getCategory());
    categoryService.saveOrUpdate(existingCategory); // don't forget to save the new category
   }

   for (TransactionEntity transaction : unCategorizedTransactions) {
    for (String query : autoCategory.getQueries()) {
// TODO handle if description is NULL
     if (transaction.getEntity().toLowerCase().contains(query.toLowerCase()) ||
             transaction.getDescription().toLowerCase().contains(query.toLowerCase())) {
      transaction.addCategory(existingCategory);
      transactionRepository.save(transaction);
      numCategorized++;
      break;
     }
    }
   }
  }
  message = "Transactions categorized: "+ numCategorized;
  model.addAttribute("message", message);
  log.info(message);
  return "redirect:/auto-category";
 }


}
