package nl.marisabel.transactions.classes;

import lombok.extern.log4j.Log4j2;
import nl.marisabel.transactions.categories.classes.CategoryEntity;
import nl.marisabel.transactions.categories.interfaces.CategoryRepository;
import nl.marisabel.transactions.interfaces.TransactionService;
import nl.marisabel.transactions.interfaces.TransactionRepository;
import nl.marisabel.transactions.charts.ChartService;
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
 */
@Service
@Log4j2
public class TransactionServiceImp implements TransactionService {

 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 public TransactionServiceImp(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }

 /**
  * Find all transactions
  * @return list of transactions
  */
 public List<TransactionEntity> findAll(){
  return transactionRepository.findAll();
 }

 /**
  * Find all transactions in pages
  * @param pageable
  * @return list of pages containing x number of transactions
  */
 public Page<TransactionEntity> findAllPageable(Pageable pageable) {
  return transactionRepository.findAll(pageable);
 }

 /**
  * find single transacion by ID
  * @param id
  * @return transaction object
  */
 public TransactionEntity getTransaction(Long id) {
  return transactionRepository.findById(id).get();
 }

 /**
  * find transactions without categories
  * @param pageable
  * @return list of transactions without categories
  */
 public Page<TransactionEntity> findByCategoriesIsEmpty(Pageable pageable) {
  return transactionRepository.findByCategoriesIsEmpty(pageable);
 }


 /**
  * Get Pages of Transactions list based matching category
  *
  * @param categoryId
  * @param pageRequest
  * @return list of pageable transactions matching category ID
  */
 public Page<TransactionEntity> getTransactionsByCategory(Long categoryId, PageRequest pageRequest) {
  return transactionRepository.findByCategoryIdPageable(categoryId, pageRequest);
 }

 /**
  *<h2>Chart Filter of Transactions</h2>
  * <p>find transactions between dates and categories</p>
  *
  * mainly used for the monthly category chart where:</br>
  * used in: {@link ChartService#getMonthlyDebitsByCategoryForCategory(int year, CategoryEntity categoryEntity) getMonthlyDebitsByCategoryForCategory()}</br>
  * and in: {@link ChartService#getMonthlyCreditsByCategoryForCategory(int year, CategoryEntity categoryEntity) getMonthlyCreditsByCategoryForCategory()}
  *
  * @param startDate = start of month
  * @param endDate = end of month
  * @param category = categoryID and transactions to display
  * @return list of transactions belonging to a specific category during a range of dates
  */

 public List<TransactionEntity> findAllByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, CategoryEntity category){
  return transactionRepository.findAllByDateBetweenAndCategory(startDate,endDate,category);
 }



 /**
  * <h2>POST: ADVANCED FILTERING OF TRANSACTIONS</h2>
  * POST filters the transactions using the Transaction Specification in repository
  *
  * @param filter
  * @param pageable
  * @return all matching transactions
  */
 public Page<TransactionEntity> filterTransactions(TransactionFilter filter, Pageable pageable) {
  return transactionRepository.findAll(new TransactionSpecification(filter), pageable);
 }


 /**
  * <h3>Get transactions for each month of each year</h3>
  * stream transactions and separates them from start of month to end of month
  * then map them to MMyyyy
  *
  * @return map of transactions grouped in each matching month/year
  */
 public Map<String, String> getDistinctMonthsAndYears() {
  List<TransactionEntity> transactions = this.findAll();

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
  * <h3>GET: ADVANCED FILTERING OF TRANSACTIONS</h3>
  * GET filters the transactions using the Transaction Specification in repository
  *
  * @param filter
  * @return all matching transactions
  */
 public Object getFilteredTransactions(TransactionFilter filter) {
  Specification<TransactionEntity> specification = new TransactionSpecification(filter);
  return transactionRepository.findAll(specification);
 }

 /**
  * <H3>CREATE SORTED PAGES FOR FILTERS</H3>
  * create pages according to size of list
  * and sorts columns by entity name, amount or date
  *
  * @param sort
  * @param page
  * @param size
  * @return page of transactions
  */
 public Pageable createPageable(String sort, int page, int size) {
  if (sort.contains("entity")) {
   if (sort.contains(",desc")) {
    return PageRequest.of(page, size, Sort.by("entity").descending());
   } else {
    return PageRequest.of(page, size, Sort.by("entity").ascending());
   }
  } else if (sort.contains("amount")) {
   if (sort.contains(",desc")) {
    return PageRequest.of(page, size, Sort.by("amount").descending());
   } else {
    return PageRequest.of(page, size, Sort.by("amount").ascending());
   }
  } else if (sort.contains("date")) {
   if (sort.contains(",desc")) {
    return PageRequest.of(page, size, Sort.by("date").descending());
   } else {
    return PageRequest.of(page, size, Sort.by("date").ascending());
   }
  }
  return PageRequest.of(page, size, Sort.by("date").ascending());
 }





}
