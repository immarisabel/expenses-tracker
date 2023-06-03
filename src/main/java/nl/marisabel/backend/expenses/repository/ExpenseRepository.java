package nl.marisabel.backend.expenses.repository;

import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

 List<ExpenseEntity> findByDescriptionContainingIgnoreCaseOrEntityContainingIgnoreCase(String descriptionKeyword, String entityKeyword);

 @Query("SELECT amount FROM ExpenseEntity")
 List<Integer> findAllAmounts();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Credit'")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpenseEntity WHERE creditOrDebit = 'Debit'")
 int calculateTotalDebits();


}