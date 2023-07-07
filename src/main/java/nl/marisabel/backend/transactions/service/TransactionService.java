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

 List<TransactionEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category);

 Page<TransactionEntity> getTransactionsByCategory(Long categoryId, PageRequest pageRequest);

 Page<TransactionEntity> filterTransactions(TransactionFilter filter, Pageable pageable);
 Object getFilteredTransactions(TransactionFilter filter);

 Map<String, String> getDistinctMonthsAndYears();

 // Other

 Pageable createPageable(String sort, int page, int size);


}
