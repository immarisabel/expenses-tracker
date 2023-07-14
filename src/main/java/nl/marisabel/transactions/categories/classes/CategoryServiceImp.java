package nl.marisabel.transactions.categories.classes;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.interfaces.CategoryRepository;
import nl.marisabel.transactions.categories.interfaces.CategoryService;
import nl.marisabel.transactions.classes.TransactionEntity;
import nl.marisabel.transactions.interfaces.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <H2>CATEGORY SERVICE IMPLEMENTATION</H2>
 * handles the business logic related to categories in transactions
 * TODO: prevent duplicated categories
 */
@Service
@Log4j2
public class CategoryServiceImp implements CategoryService {

 private final AutoCategoryServiceImp autoCategoryService;
 private final CategoryRepository categoryRepository;
 private final TransactionRepository transactionRepository;

 public CategoryServiceImp(AutoCategoryServiceImp autoCategoryService, CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
  this.autoCategoryService = autoCategoryService;
  this.categoryRepository = categoryRepository;
  this.transactionRepository = transactionRepository;
 }

 /**
  * <h2>GET ALL CATEGORIES</h2>
  *
  * @return <b>LIST</b> of categories sorted in <b>ASC</b> alphabetically
  */
 public List<CategoryEntity> getCategories() {
  List<CategoryEntity> category = new ArrayList<>();
  categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")).forEach(category::add);
  return category;
 }

 /**
  * <H2>GET SPECIFIC CATEGORY</H2>
  *
  * @param id
  * @return single object of <B>CategoryEntity</B>
  */
 public CategoryEntity getCategory(Long id) {
  return categoryRepository.findById(id).get();
 }

 /**
  * <H2>FIND BY CATEGORY STRING NAME</H2>
  *
  * @param category
  * @return single object of <B>CategoryEntity</B>
  */
 @Override
 public CategoryEntity findByCategory(String category) {
  return categoryRepository.findByCategory(category);
 }

 /**
  * <H3>CategoryEntity CRUD functions</H3>
  */
 public void saveOrUpdate(CategoryEntity category) {
  categoryRepository.save(category);
 }


 public void deleteCategory(Long id) {
  categoryRepository.deleteById(id);
 }

 private CategoryEntity createCategory(String category) {
  CategoryEntity newCategory = new CategoryEntity();
  newCategory.setCategory(category);
  return categoryRepository.save(newCategory);
 }


 /**
  * <H2>AUTO-CATEGORIZE TRANSACTIONS</H2>
  * Filters transactions without categories and assign category according to matching description and entity.</br>
  * Does not apply if transaction already has a category.</br>
  * To update such transactions, we need to remove the categories from them.</br>
  * TODO: method to remove category in batch
  * @return number fo transactions categorized
  */
 public int autoCategorizeTransactions() {
  // load all transactions without category

  List<TransactionEntity> unCategorizedTransactions = transactionRepository.findByCategoriesEmpty();
  log.info("Number of uncategorized transactions: " + unCategorizedTransactions.size());
  List<AutoCategoryEntity> autoCategories = autoCategoryService.getAutoCategoriesList();

  int numCategorized = 0;

  for (AutoCategoryEntity autoCategory : autoCategories) {
   // Try to find an existing CategoryEntity with this category
   CategoryEntity existingCategory = findByCategory(autoCategory.getCategory());

   // If none exists, create a new one
   if (existingCategory == null) {
    existingCategory = new CategoryEntity();
    existingCategory.setCategory(autoCategory.getCategory());
    saveOrUpdate(existingCategory); // don't forget to save the new category
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
  return numCategorized;
 }


 /**
  * <h2>BATCH UPDATE CATEGORIES</h2>
  * process the category for all selected transactions via JS
  * @param categoryId
  * @param transactionIds
  */
 @Transactional
 public void batchUpdateCategory(Long categoryId, List<Long> transactionIds) {
  CategoryEntity category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

  List<TransactionEntity> transactions = transactionRepository.findAllById(transactionIds);

  log.info("....transactions updated: " + transactions.size());

  for (TransactionEntity transaction : transactions) {
   transaction.getCategories().clear();
   transaction.addCategory(category);
   transactionRepository.save(transaction);
  }
 }


}
