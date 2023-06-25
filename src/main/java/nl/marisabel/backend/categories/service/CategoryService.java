package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
    public class CategoryService {

        @Autowired
        CategoryRepository categoryRepository;

        public List<CategoryEntity> getCategories(){
            List<CategoryEntity> category = new ArrayList<>();
            categoryRepository.findAll().forEach(category::add);
            return category;
        }

        public CategoryEntity getCategory(Long id){
            return categoryRepository.findById(id).get();
        }

        public void saveOrUpdate(CategoryEntity category) {
            categoryRepository.save(category);
        }

        public void deleteCategory(Long id) {
            categoryRepository.deleteById(id);
        }


    public CategoryEntity getOrCreateCategory(AutoCategoryEntity autoCategory) {
        CategoryEntity existingCategory = categoryRepository.findByCategory(autoCategory.getCategory());

        if (existingCategory == null) {
            existingCategory = createCategory(autoCategory.getCategory());
        }

        return existingCategory;
    }

    private CategoryEntity createCategory(String category) {
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setCategory(category);
        return categoryRepository.save(newCategory);
    }

    public boolean isTransactionMatch(TransactionEntity transaction, String query) {
        String transactionEntity = transaction.getEntity().toLowerCase();
        String transactionDescription = transaction.getDescription().toLowerCase();
        String queryLower = query.toLowerCase();

        return transactionEntity.contains(queryLower) || transactionDescription.contains(queryLower);
    }
}
