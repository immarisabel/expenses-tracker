package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.entity.CategoryEntity;

import java.util.List;

public interface CategoryService  {

 public List<CategoryEntity> getCategories();
 public CategoryEntity getCategory(Long id);

 public void saveOrUpdate(CategoryEntity category);

 public CategoryEntity findByCategory(String category);

 public void deleteCategory(Long id);

}
