package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
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
public class CategoryServiceImp implements CategoryService {

 private final CategoryRepository categoryRepository;

 public CategoryServiceImp(CategoryRepository categoryRepository) {
  this.categoryRepository = categoryRepository;
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


}
