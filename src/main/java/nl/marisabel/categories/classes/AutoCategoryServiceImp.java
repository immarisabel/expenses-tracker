package nl.marisabel.categories.classes;

import nl.marisabel.categories.interfaces.AutoCategoryRepository;
import nl.marisabel.categories.interfaces.AutoCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <H2>CATEGORY SERVICE IMPLEMENTATION</H2>
 * handles the business logic related to auto-categories
 * They are not linked to transactions or categories.
 * Their purpose is to <b>populate the categories inside transactions</b> according to the queries given
 * TODO: prevent duplicated auto-categories
 */
@Service
public class AutoCategoryServiceImp implements AutoCategoryService {

 private final AutoCategoryRepository autoCategoryRepository;

 public AutoCategoryServiceImp(AutoCategoryRepository autoCategoryRepository) {
  this.autoCategoryRepository = autoCategoryRepository;
 }


 /**
  * <h2>SAVE or UPDATE AUTOCATEGORY</h2>
  * @param autoCategory
  */
 @Override
 public void saveAutocategory(AutoCategoryEntity autoCategory) {
  autoCategoryRepository.save(autoCategory);
 }

 /**
  * <h2>GET AUTO-CATEGORY LIST</h2>
  * @return list of all auto-categories objects
  */
 @Override
 public List<AutoCategoryEntity> getAutoCategoriesList() {
  return autoCategoryRepository.findAll();
 }

 /**
  * <h2>GET AUTO-CATEGORY TO EDIT</h2>
  * @param id
  * @return single Auto Category object by ID
  */
 @Override
 public AutoCategoryEntity getAutocategoryToEdit(Long id) {
  return autoCategoryRepository.getReferenceById(id);
 }




}
