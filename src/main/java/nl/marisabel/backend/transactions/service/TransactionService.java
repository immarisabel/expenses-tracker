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

@Service
@Log4j2
public class TransactionService {

 private final TransactionRepository transactionRepository;
 private final CategoryRepository categoryRepository;

 @Autowired
 public TransactionService(
         TransactionRepository transactionRepository,
         CategoryRepository categoryRepository
 ) {
  this.transactionRepository = transactionRepository;
  this.categoryRepository = categoryRepository;
 }


 // FILTERS

 public TransactionEntity getTransaction(Long id) {
  return transactionRepository.findById(id).get();
 }


 public Page<TransactionEntity> searchTransactions(String searchTerm, PageRequest pageRequest) {
  return transactionRepository.findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, pageRequest);
 }

 public Page<TransactionEntity> filterTransactionByDate(LocalDate startDate, LocalDate endDate, Pageable pageable) {
  return transactionRepository.findByDateBetween(startDate, endDate, pageable);
 }

 public Page<TransactionEntity> getTransactionsByCategory(Long categoryId, PageRequest pageRequest) {
  return transactionRepository.findByCategoryIdPageable(categoryId, pageRequest);
 }

 // CHARTS

 public List<TransactionEntity> getAllTransactions() {
  return transactionRepository.findAll();
 }

 public double calculateRemainingFundsByMonth(LocalDate startDate, LocalDate endDate) {
  double totalCredits = transactionRepository.calculateTotalCreditsByMonth(startDate, endDate);
  double totalDebits = transactionRepository.calculateTotalDebitsByMonth(startDate, endDate);
  return totalCredits - totalDebits;
 }


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

 // MANIPULATION

 @Transactional
 public void batchUpdateCategory(Long categoryId, List<Long> transactionIds) {
  CategoryEntity category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

  List<TransactionEntity> transactions = transactionRepository.findAllById(transactionIds);

  log.info("....transactions updated: " + transactions.size());

  for (TransactionEntity transaction : transactions) {
   transaction.getCategories().clear();
   transaction.addCategory(category);
   transactionRepository.save(transaction);
  }
 }


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

  // Default case
  return PageRequest.of(page, size, Sort.by("date").ascending());
 }

 public Page<TransactionEntity> filterTransactionByAmount(double minAmount, double maxAmount, PageRequest pageRequest) {
  return transactionRepository.findByAmountBetween(minAmount, maxAmount, pageRequest);
 }

 public Page<TransactionEntity> filterTransactions(TransactionFilter filter, Pageable pageable) {
  return transactionRepository.findAll(new TransactionSpecification(filter), pageable);
 }

 public Object getFilteredTransactions(TransactionFilter filter) {
  Specification<TransactionEntity> specification = new TransactionSpecification(filter);
  return transactionRepository.findAll(specification);
 }


}
