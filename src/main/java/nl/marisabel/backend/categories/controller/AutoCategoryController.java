package nl.marisabel.backend.categories.controller;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.AutoCategoryRepository;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.categories.service.CategoryService;
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

 private final AutoCategoryRepository autoCategoryRepository;
 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;
 private final CategoryService categoryService;
 private String message;

 public AutoCategoryController(AutoCategoryRepository autoCategoryRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository, CategoryService categoryService) {
  this.autoCategoryRepository = autoCategoryRepository;
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
  this.categoryService = categoryService;
 }

 /**
  * Load the form on /auto-category
  * @param model auto-category.html
  * */
 @GetMapping
 public String getAutoCategoryForm(Model model) {
  model.addAttribute("autoCategoryList", autoCategoryRepository.findAll());
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
  autoCategoryRepository.save(autoCategoryEntity);
  return "redirect:/auto-category";
 }

 /**
  * Load the data of the given category query (id) on the form in order to edit
  * @param id id of the autoCategory to be updated
  * @return redirect:/auto-category
  */
 @GetMapping("/update")
 public String showFormForUpdatingAutoCategory(@RequestParam("id") Long id, Model model) {
  model.addAttribute("autoCategoryEntity", autoCategoryRepository.getReferenceById(id));
  model.addAttribute("autoCategoryList", autoCategoryRepository.findAll());
  return "categories/auto-category";
 }

 @PostMapping("/update")
 public String updateAutoCategory(@ModelAttribute("autoCategoryEntity") AutoCategoryEntity autoCategoryEntity, Model model) {
  autoCategoryRepository.save(autoCategoryEntity);
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

  List<AutoCategoryEntity> autoCategories = autoCategoryRepository.findAll();

  int numCategorized = 0;

  for (AutoCategoryEntity autoCategory : autoCategories) {
   // Try to find an existing CategoryEntity with this category
   CategoryEntity existingCategory = categoryRepository.findByCategory(autoCategory.getCategory());

   // If none exists, create a new one
   if (existingCategory == null) {
    existingCategory = new CategoryEntity();
    existingCategory.setCategory(autoCategory.getCategory());
    categoryRepository.save(existingCategory); // don't forget to save the new category
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
