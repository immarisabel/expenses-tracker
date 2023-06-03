package nl.marisabel.backend.expenses.service;

import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


 @Service
 public class ExpenseService {

  @Autowired
  public ExpenseService(ExpenseRepository expenseRepository) {
  }
 }

