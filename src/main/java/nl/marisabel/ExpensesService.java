package nl.marisabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class ExpensesService {

 @Service
 public class ExpenseService {
  private final ExpenseRepository expenseRepository;

  @Autowired
  public ExpenseService(ExpenseRepository expenseRepository) {
   this.expenseRepository = expenseRepository;
  }

 }
}
