package nl.marisabel.backend.transactions.service;

import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.model.TransactionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService {

 // FILTERS
 public TransactionEntity getTransaction(Long id);

 public Page<TransactionEntity> searchTransactions(String searchTerm,
                                                   PageRequest pageRequest);

 public Page<TransactionEntity> filterTransactionByDate(LocalDate startDate,
                                                        LocalDate endDate,
                                                        Pageable pageable);

 public Page<TransactionEntity> getTransactionsByCategory(Long categoryId,
                                                          PageRequest pageRequest);

 // CHARTS
 public List<TransactionEntity> getAllTransactions();

 public Map<String, String> getDistinctMonthsAndYears();



 public Pageable createPageable(String sort, int page, int size);

 public Page<TransactionEntity> filterTransactionByAmount(double minAmount,
                                                          double maxAmount,
                                                          PageRequest pageRequest);

 public Page<TransactionEntity> filterTransactions(TransactionFilter filter,
                                                   Pageable pageable);

 public Object getFilteredTransactions(TransactionFilter filter);
}
