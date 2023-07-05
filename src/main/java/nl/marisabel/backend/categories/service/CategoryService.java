package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryService {

 @Autowired
 CategoryRepository categoryRepository;

    /**
     * GET ALL CATEGORIES
     * @return list of categories sorted in ASC alphabetically
     */
 public List<CategoryEntity> getCategories() {
  List<CategoryEntity> category = new ArrayList<>();
  categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "category")).forEach(category::add);
  return category;
 }

    /**
     * GET SPECIFIC CATEGORY
     * @param id
     * @return single object of CategoryEntity
     */
 public CategoryEntity getCategory(Long id) {
  return categoryRepository.findById(id).get();
 }

    /**
     * CategoryEntity CRUD functions
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

}
