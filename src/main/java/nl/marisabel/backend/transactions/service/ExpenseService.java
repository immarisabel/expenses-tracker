package nl.marisabel.backend.transactions.service;

import jakarta.transaction.Transactional;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;
import nl.marisabel.backend.transactions.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

 private final ExpenseRepository expenseRepository;
 private final CategoryRepository categoryRepository;

 @Autowired
 public ExpenseService(
         ExpenseRepository expenseRepository,
         CategoryRepository categoryRepository
 ) {
  this.expenseRepository = expenseRepository;
  this.categoryRepository = categoryRepository;
 }

 public List<ExpenseEntity> searchExpenses(String searchTerm) {
  String searchTermLower = searchTerm.toLowerCase();
  return expenseRepository.findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTermLower);
 }

 @Transactional
 public void batchUpdateCategory(Long categoryId, List<ExpenseEntity> expenses) {
  CategoryEntity category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

  for (ExpenseEntity expense : expenses) {
   expense.getCategories().clear();
   expense.addCategory(category);
   expenseRepository.save(expense);
  }
 }

 public List<ExpenseEntity> filterExpensesByDate(LocalDate startDate, LocalDate endDate) {
  return expenseRepository.findByDateBetween(startDate, endDate);
 }
}
