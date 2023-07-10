package nl.marisabel.transactions.interfaces;

import nl.marisabel.transactions.categories.classes.CategoryEntity;
import nl.marisabel.transactions.classes.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

 // SEARCHING & FILTERING

 Page<TransactionEntity> findAll(Pageable pageable);

 @Query("SELECT t FROM TransactionEntity t WHERE t.categories IS EMPTY")
 List<TransactionEntity> findByCategoriesEmpty();

 @Query("SELECT t FROM TransactionEntity t WHERE t.categories IS EMPTY")
 Page<TransactionEntity> findByCategoriesIsEmpty(Pageable pageable);

 @Query("SELECT e FROM TransactionEntity e WHERE lower(e.entity) LIKE lower(concat('%', :searchTerm, '%')) OR lower(e.description) LIKE lower(concat('%', :searchTerm, '%'))")
 Page<TransactionEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

 List<TransactionEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

 Page<TransactionEntity> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

 @Query("SELECT amount FROM TransactionEntity")
 List<Integer> findAllAmounts();

 @Query("SELECT DISTINCT e FROM TransactionEntity e JOIN e.categories c WHERE c.id = :categoryId")
 Page<TransactionEntity> findByCategoryIdPageable(@Param("categoryId") Long categoryId, Pageable pageable);

 Page<TransactionEntity> findByAmountBetween(double minAmount, double maxAmount, Pageable pageable);

// UPLOADING

 @Query("SELECT COUNT(e) > 0 FROM TransactionEntity e WHERE e.date = :date AND e.entity = :entity AND e.creditOrDebit = :creditOrDebit AND e.amount = :amount AND e.description = :description")
 boolean transactionExists(
         @Param("date") LocalDate date,
         @Param("entity") String entity,
         @Param("creditOrDebit") String creditOrDebit,
         @Param("amount") double amount,
         @Param("description") String description);

//  CHARTS

 @Query("SELECT e FROM TransactionEntity e JOIN e.categories c WHERE e.date BETWEEN :startDate AND :endDate AND c = :category")
 List<TransactionEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category);

 @Query("SELECT COALESCE(SUM(amount), 0) FROM TransactionEntity WHERE creditOrDebit = 'Credit' OR (creditOrDebit = 'Bij' AND 'Credit' = 'Credit')")
 int calculateTotalCredits();

 @Query("SELECT COALESCE(SUM(amount), 0) FROM TransactionEntity WHERE creditOrDebit = 'Debit' OR (creditOrDebit = 'Af' AND 'Debit' = 'Debit')")
 int calculateTotalDebits();


 // SAVINGS

 @Query("SELECT COALESCE(SUM(amount), 0.0) FROM TransactionEntity WHERE creditOrDebit = 'Credit' AND date BETWEEN :startDate AND :endDate")
 double calculateTotalCreditsByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

 @Query("SELECT COALESCE(SUM(amount), 0.0) FROM TransactionEntity WHERE creditOrDebit = 'Debit' AND date BETWEEN :startDate AND :endDate")
 double calculateTotalDebitsByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}

