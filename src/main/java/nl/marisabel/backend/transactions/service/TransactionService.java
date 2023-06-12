package nl.marisabel.backend.transactions.service;

import jakarta.transaction.Transactional;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
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

 public List<TransactionEntity> searchTransactions(String searchTerm) {
  String searchTermLower = searchTerm.toLowerCase();
  return transactionRepository.findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTermLower);
 }

 @Transactional
 public void batchUpdateCategory(Long categoryId, List<TransactionEntity> transactionEntities) {
  CategoryEntity category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

  for (TransactionEntity transaction : transactionEntities) {
   transaction.getCategories().clear();
   transaction.addCategory(category);
   transactionRepository.save(transaction);
  }
 }

 public List<TransactionEntity> filterTransactionByDate(LocalDate startDate, LocalDate endDate) {
  return transactionRepository.findByDateBetween(startDate, endDate);
 }

 public Page<TransactionEntity> filterTransactionByDate(LocalDate startDate, LocalDate endDate, PageRequest of) {
  return transactionRepository.findByDateBetween(startDate, endDate, of);
 }

 public List<TransactionEntity> getTransactionsByCategory(Long categoryId) {
  return transactionRepository.findByCategoryId(categoryId);
 }

 public double calculateTotalCreditsByMonth(LocalDate startDate, LocalDate endDate) {
  return transactionRepository.calculateTotalCreditsByMonth(startDate, endDate);
 }

 public double calculateTotalDebitsByMonth(LocalDate startDate, LocalDate endDate) {
  return transactionRepository.calculateTotalDebitsByMonth(startDate, endDate);
 }


 public double calculateRemainingFundsByMonth(LocalDate startDate, LocalDate endDate) {
  double totalCredits = transactionRepository.calculateTotalCreditsByMonth(startDate, endDate);
  double totalDebits = transactionRepository.calculateTotalDebitsByMonth(startDate, endDate);
  return totalCredits - totalDebits;
 }


}
