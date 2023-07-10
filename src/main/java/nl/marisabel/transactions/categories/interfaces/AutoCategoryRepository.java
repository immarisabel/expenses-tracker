package nl.marisabel.transactions.categories.interfaces;

import nl.marisabel.transactions.categories.classes.AutoCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoCategoryRepository extends JpaRepository<AutoCategoryEntity, Long> {

    @Query("SELECT a FROM AutoCategoryEntity a WHERE :query MEMBER OF a.queries")
    List<AutoCategoryEntity> findByQuery(@Param("query") String query);
}
