package nl.marisabel.backend.transactions.repository;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

 Page<ExpenseEntity> findAll(Pageable pageable);

 @Query("SELECT e FROM ExpenseEntity e WHERE lower(e.entity) LIKE lower(concat('%', :searchTerm, '%')) OR lower(e.description) LIKE lower(concat('%', :searchTerm, '%'))")
 Page<ExpenseEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchTerm, Pageable pageable);

 List<ExpenseEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

 boolean existsByDescriptionIgnoreCase(String description);
 boolean existsByDescriptionIgnoreCaseAndEntityIgnoreCase(String description, String entity);


 @Query("SELECT e FROM ExpenseEntity e JOIN e.categories c WHERE e.date BETWEEN :startDate AND :endDate AND c = :category")
 List<ExpenseEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category);

 @Query("SELECT amount FROM ExpenseEntity")
 List<Integer> findAllAmounts();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Credit' OR (creditOrDebit = 'Bij' AND 'Credit' = 'Credit')")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Debit' OR (creditOrDebit = 'Af' AND 'Debit' = 'Debit')")
 int calculateTotalDebits();
}
