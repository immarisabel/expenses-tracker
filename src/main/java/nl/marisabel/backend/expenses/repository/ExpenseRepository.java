package nl.marisabel.backend.expenses.repository;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

 @Query("SELECT e FROM ExpenseEntity e JOIN e.categories c WHERE e.date BETWEEN :startDate AND :endDate AND c = :category")
 List<ExpenseEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category);


 Page<ExpenseEntity> findAll(Pageable pageable);

// @Query("SELECT e FROM ExpenseEntity e WHERE lower(e.entity) LIKE lower(concat('%', :searchTerm, '%')) OR lower(e.description) LIKE lower(concat('%', :searchTerm, '%'))")
// List<ExpenseEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

 @Query("SELECT e FROM ExpenseEntity e WHERE lower(e.entity) LIKE lower(concat('%', :searchTerm, '%')) OR lower(e.description) LIKE lower(concat('%', :searchTerm, '%'))")
 Page<ExpenseEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchTerm, Pageable pageable);

 boolean existsByDescriptionIgnoreCase(String description);

 @Query("SELECT amount FROM ExpenseEntity")
 List<Integer> findAllAmounts();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Credit' OR (creditOrDebit = 'Bij' AND 'Credit' = 'Credit')")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Debit' OR (creditOrDebit = 'Af' AND 'Debit' = 'Debit')")
 int calculateTotalDebits();


 List<ExpenseEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);



}