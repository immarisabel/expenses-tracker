package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.AutoCategoryRepository;

import java.util.List;

public interface AutoCategoryService {

 public void saveAutocategory(AutoCategoryEntity autoCategory);
 public List<AutoCategoryEntity> getAutoCategoriesList();
 public AutoCategoryEntity getAutocategoryToEdit(Long id);


}
