package nl.marisabel.backend.transactions.repository;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

 // SEARCHING & FILTERING

 Page<ExpenseEntity> findAll(Pageable pageable);

 @Query("SELECT e FROM ExpenseEntity e WHERE lower(e.entity) LIKE lower(concat('%', :searchTerm, '%')) OR lower(e.description) LIKE lower(concat('%', :searchTerm, '%'))")
 List<ExpenseEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

 List<ExpenseEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

 @Query("SELECT amount FROM ExpenseEntity")
 List<Integer> findAllAmounts();

// UPLOADING

 @Query("SELECT COUNT(e) > 0 FROM ExpenseEntity e WHERE e.date = :date AND e.entity = :entity AND e.creditOrDebit = :creditOrDebit AND e.amount = :amount AND e.description = :description")
 boolean transactionExists(
         @Param("date") LocalDate date,
         @Param("entity") String entity,
         @Param("creditOrDebit") String creditOrDebit,
         @Param("amount") double amount,
         @Param("description") String description);

//  CHARTS

 @Query("SELECT e FROM ExpenseEntity e JOIN e.categories c WHERE e.date BETWEEN :startDate AND :endDate AND c = :category")
 List<ExpenseEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category);

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Credit' OR (creditOrDebit = 'Bij' AND 'Credit' = 'Credit')")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Debit' OR (creditOrDebit = 'Af' AND 'Debit' = 'Debit')")
 int calculateTotalDebits();


 Page<ExpenseEntity> findByDateBetween(LocalDate startDate, LocalDate endDate, PageRequest of);
}
