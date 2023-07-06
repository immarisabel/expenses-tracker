package nl.marisabel.backend.transactions.service;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.model.TransactionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService {

 // Retrieval

 List<TransactionEntity> findAll();

 Page<TransactionEntity> findAllPageable(Pageable pageable);

 TransactionEntity getTransaction(Long id);

 Page<TransactionEntity> findByCategoriesIsEmpty(Pageable pageable);
 Page<TransactionEntity> findByCategoryIdPageable(Long categoryId, Pageable pageable);

 Page<TransactionEntity> findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchTerm, Pageable pageable);

 List<TransactionEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

 Page<TransactionEntity> findByDateBetweenPageable(LocalDate startDate, LocalDate endDate, Pageable pageable);

 List<Integer> findAllAmounts();

 List<TransactionEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category);
 // Filtering

 Page<TransactionEntity> searchTransactions(String searchTerm, PageRequest pageRequest);

 Page<TransactionEntity> filterTransactionByDate(LocalDate startDate, LocalDate endDate, Pageable pageable);

 Page<TransactionEntity> getTransactionsByCategory(Long categoryId, PageRequest pageRequest);

 Page<TransactionEntity> filterTransactionByAmount(double minAmount, double maxAmount, PageRequest pageRequest);

 Page<TransactionEntity> filterTransactions(TransactionFilter filter, Pageable pageable);
 Object getFilteredTransactions(TransactionFilter filter);

 // Charting

 List<TransactionEntity> getAllTransactions();

 Map<String, String> getDistinctMonthsAndYears();

// // Calculation
//
// int calculateTotalCredits();
//
// int calculateTotalDebits();
//
// double calculateTotalCreditsByMonth(LocalDate startDate, LocalDate endDate);
//
// double calculateTotalDebitsByMonth(LocalDate startDate, LocalDate endDate);

 // Other

 Pageable createPageable(String sort, int page, int size);


}
