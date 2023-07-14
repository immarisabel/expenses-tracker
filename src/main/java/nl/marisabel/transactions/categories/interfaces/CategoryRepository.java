package nl.marisabel.transactions.categories.interfaces;

import nl.marisabel.transactions.categories.classes.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

 CategoryEntity findByCategory(String category);

}
