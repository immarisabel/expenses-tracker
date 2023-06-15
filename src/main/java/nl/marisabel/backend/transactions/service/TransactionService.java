package nl.marisabel.backend.transactions.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


 public List<TransactionEntity> filterTransactionByDate(LocalDate startDate, LocalDate endDate) {
  return transactionRepository.findByDateBetween(startDate, endDate);
 }


 public List<TransactionEntity> getTransactionsByCategory(Long categoryId) {
  return transactionRepository.findByCategoryId(categoryId);
 }


 public double calculateRemainingFundsByMonth(LocalDate startDate, LocalDate endDate) {
  double totalCredits = transactionRepository.calculateTotalCreditsByMonth(startDate, endDate);
  double totalDebits = transactionRepository.calculateTotalDebitsByMonth(startDate, endDate);
  return totalCredits - totalDebits;
 }


 public List<TransactionEntity> getAllTransactions() {
  return transactionRepository.findAll();
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
 public void batchUpdateCategory(Long categoryId, List<Long> transactionIds, int pageNumber, int pageSize) {
  CategoryEntity category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

  int startIndex = pageNumber * pageSize;
  int endIndex = Math.min(startIndex + pageSize, transactionIds.size());
  List<Long> paginatedTransactionIds = transactionIds.subList(startIndex, endIndex);

  List<TransactionEntity> transactions = transactionRepository.findAllById(paginatedTransactionIds);

  log.info("....transactions updated: " + transactions.size());

  for (TransactionEntity transaction : transactions) {
   transaction.getCategories().clear();
   transaction.addCategory(category);
   transactionRepository.save(transaction);
  }
 }




}
