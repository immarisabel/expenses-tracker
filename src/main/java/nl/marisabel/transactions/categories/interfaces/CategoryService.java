package nl.marisabel.transactions.categories.interfaces;

import nl.marisabel.transactions.categories.classes.CategoryEntity;

import java.util.List;

public interface CategoryService  {

 public List<CategoryEntity> getCategories();
 public CategoryEntity getCategory(Long id);

 public void saveOrUpdate(CategoryEntity category);

 public CategoryEntity findByCategory(String category);

 public void deleteCategory(Long id);

}
