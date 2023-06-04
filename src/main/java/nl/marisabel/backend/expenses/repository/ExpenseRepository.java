package nl.marisabel.backend.expenses.repository;

import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

 //List<ExpenseEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String entity, String description);

 @Query("SELECT e FROM ExpenseEntity e WHERE lower(e.entity) LIKE lower(concat('%', :searchTerm, '%')) OR lower(e.description) LIKE lower(concat('%', :searchTerm, '%'))")
 List<ExpenseEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

 boolean existsByDescriptionIgnoreCase(String description);

 @Query("SELECT amount FROM ExpenseEntity")
 List<Integer> findAllAmounts();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Credit'")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Debit'")
 int calculateTotalDebits();

 List<ExpenseEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);



}