package nl.marisabel.transactions.categories.interfaces;

import nl.marisabel.transactions.categories.classes.AutoCategoryEntity;

import java.util.List;

public interface AutoCategoryService {

 public void saveAutocategory(AutoCategoryEntity autoCategory);
 public List<AutoCategoryEntity> getAutoCategoriesList();
 public AutoCategoryEntity getAutocategoryToEdit(Long id);


}
