package nl.marisabel.backend.transactions.controller;

import lombok.Getter;
import lombok.Setter;
import nl.marisabel.backend.transactions.entity.ExpenseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExpenseUpdateForm {

    private List<ExpenseEntity> expenses;
    private Long categoryId;


    // Default constructor
    public ExpenseUpdateForm() {
        this.expenses = new ArrayList<>();
    }

    // Add a convenience method to add an expense to the list
    public void addExpense(ExpenseEntity expense) {
        this.expenses.add(expense);
    }
}