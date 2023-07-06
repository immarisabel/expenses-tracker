package nl.marisabel.backend.transactions.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.model.TransactionFilter;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.repository.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

/**
 * <H2>TRANSACTIONS SERVICE IMPLEMENTATION</H2>
 * handles the business logic related to transactions
 * filtering and calculations for charts
 * TODO: prevent duplicated categories
 */
@Service
@Log4j2
public class TransactionServiceImp implements TransactionService{

 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 @Autowired
 public TransactionServiceImp(
         TransactionRepository transactionRepository,
         CategoryRepository categoryRepository
 ) {
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }

 /**
  * <h2>GET ALL TRANSACTIONS</h2>
  * @return list of transactions
  */
 public List<TransactionEntity> getAllTransactions() {
  return transactionRepository.findAll();
 }

 /**
  * <h2>FILTERING TRANSACTIONS</h2>
  * <H3>Get one transaction by ID</H3>
  * @param id
  * @return
  */
 public TransactionEntity getTransaction(Long id) {
  return transactionRepository.findById(id).get();
 }

 /**
  * <h2>FILTERING TRANSACTIONS</h2>
  * <H3>Get Pages of Transactions list based on matching String queries</H3>
  * @param searchTerm
  * @param pageRequest
  * @return list of pageable transactions matching description or entity
  */
 public Page<TransactionEntity> searchTransactions(String searchTerm, PageRequest pageRequest) {
  return transactionRepository.findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, pageRequest);
 }

 /**
  * <h2>FILTERING TRANSACTIONS</h2>
  * <H3>Get Pages of Transactions list based on range of dates</H3>
  * @param startDate
  * @param endDate
  * @param pageable
  * @return list of pageable transactions matching date range between startDate and endDate
  */
 public Page<TransactionEntity> filterTransactionByDate(LocalDate startDate, LocalDate endDate, Pageable pageable) {
  return transactionRepository.findByDateBetween(startDate, endDate, pageable);
 }

 /**
  * <h2>FILTERING TRANSACTIONS</h2>
  * <H3>Get Pages of Transactions list based matching category</H3>
  * @param categoryId
  * @param pageRequest
  * @return list of pageable transactions matching category ID
  */
 public Page<TransactionEntity> getTransactionsByCategory(Long categoryId, PageRequest pageRequest) {
  return transactionRepository.findByCategoryIdPageable(categoryId, pageRequest);
 }

 // TODO no categories?

 /**
  * <h2>FILTERING TRANSACTIONS</h2>
  * <h3>Get transactions for each month of each year</h3>
  * stream transactions and separates them from start of month to end of month
  * then map them to MMyyyy
  * @return map of transactions grouped in each matching month/year
  */
 public Map<String, String> getDistinctMonthsAndYears() {
  List<TransactionEntity> transactions = this.getAllTransactions();

  return transactions.stream()
          .map(t -> YearMonth.from(t.getDate()))
          .distinct()
          .sorted()
          .collect(Collectors.toMap(
                  ym -> ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                  ym -> ym.format(DateTimeFormatter.ofPattern("MMyyyy")),
                  (a, b) -> b,
                  LinkedHashMap::new));
 }


 /**
  * <H2>CREATE SORTED PAGES FOR FILTERS</H2>
  * create pages according to size of list
  * and sorts columns by entity name, amount or date
  * @param sort
  * @param page
  * @param size
  * @return page of transactions
  */
 public Pageable createPageable(String sort, int page, int size) {
  if(sort.contains("entity")) {
   if(sort.contains(",desc")) {
    return PageRequest.of(page, size, Sort.by("entity").descending());
   } else {
    return PageRequest.of(page, size, Sort.by("entity").ascending());
   }
  } else if(sort.contains("amount")) {
   if(sort.contains(",desc")) {
    return PageRequest.of(page, size, Sort.by("amount").descending());
   } else {
    return PageRequest.of(page, size, Sort.by("amount").ascending());
   }
  } else if(sort.contains("date")) {
   if(sort.contains(",desc")) {
    return PageRequest.of(page, size, Sort.by("date").descending());
   } else {
    return PageRequest.of(page, size, Sort.by("date").ascending());
   }
  }
  return PageRequest.of(page, size, Sort.by("date").ascending());
 }

 /**
  * <h2>FILTER TRANSACTIONS BY AMOUNT</h2>
  * @param minAmount
  * @param maxAmount
  * @param pageRequest
  * @return list of transactions matching range of amount
  */
 public Page<TransactionEntity> filterTransactionByAmount(double minAmount, double maxAmount, PageRequest pageRequest) {
  return transactionRepository.findByAmountBetween(minAmount, maxAmount, pageRequest);
 }

 /**
  * <h2>POST: ADVANCED FILTERING OF TRANSACTIONS</h2>
  * POST filters the transactions using the Transaction Specification in repository
  * @param filter
  * @param pageable
  * @return all matching transactions
  */
 public Page<TransactionEntity> filterTransactions(TransactionFilter filter, Pageable pageable) {
  return transactionRepository.findAll(new TransactionSpecification(filter), pageable);
 }

 /**
  * <h2>GET: ADVANCED FILTERING OF TRANSACTIONS</h2>
  * GET filters the transactions using the Transaction Specification in repository
  * @param filter
  * @return all matching transactions
  */
 public Object getFilteredTransactions(TransactionFilter filter) {
  Specification<TransactionEntity> specification = new TransactionSpecification(filter);
  return transactionRepository.findAll(specification);
 }


}
