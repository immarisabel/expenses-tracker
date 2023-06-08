package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
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


    }
