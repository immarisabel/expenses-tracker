package nl.marisabel.categories.interfaces;

import nl.marisabel.categories.classes.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

 CategoryEntity findByCategory(String category);

}
