package nl.marisabel.backend.categories.service;

import nl.marisabel.backend.categories.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


    @Service
    public class CategoryService {

        @Autowired
        public CategoryService(CategoryRepository categoryRepository) {
        }

    }
