package nl.marisabel.backend.expenses.service;

import jakarta.transaction.Transactional;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.CategoryRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
 public class ExpenseService {

  @Autowired
  public ExpenseService(ExpenseRepository expenseRepository, ExpenseRepository expenseRepository1, CategoryRepository categoryRepository) {
   this.expenseRepository = expenseRepository1;
   this.categoryRepository = categoryRepository;
  }

 private final ExpenseRepository expenseRepository;
 private final CategoryRepository categoryRepository;


 public List<ExpenseEntity> searchExpenses(String searchTerm) {
  return expenseRepository.findByEntityContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
 }
 @Transactional
 public void batchUpdateCategory(Long categoryId, List<ExpenseEntity> expenses) {
  CategoryEntity category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

  for (ExpenseEntity expense : expenses) {
   expense.getCategories().clear();  // clear all existing categories
   expense.addCategory(category); // update category
   expenseRepository.save(expense);
  }

  }
 }

