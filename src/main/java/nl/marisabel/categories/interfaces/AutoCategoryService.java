package nl.marisabel.categories.interfaces;

import nl.marisabel.categories.classes.AutoCategoryEntity;

import java.util.List;

public interface AutoCategoryService {

 public void saveAutocategory(AutoCategoryEntity autoCategory);
 public List<AutoCategoryEntity> getAutoCategoriesList();
 public AutoCategoryEntity getAutocategoryToEdit(Long id);


}
