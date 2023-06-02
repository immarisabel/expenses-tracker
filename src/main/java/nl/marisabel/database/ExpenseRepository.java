package nl.marisabel.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpensesModel, Long> {

 List<ExpensesModel> findByDescriptionContainingIgnoreCaseOrEntityContainingIgnoreCase(String descriptionKeyword, String entityKeyword);

 @Query("SELECT amount FROM ExpensesModel")
 List<Integer> findAllAmounts();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpensesModel WHERE creditOrDebit = 'Credit'")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM ExpensesModel WHERE creditOrDebit = 'Debit'")
 int calculateTotalDebits();
}