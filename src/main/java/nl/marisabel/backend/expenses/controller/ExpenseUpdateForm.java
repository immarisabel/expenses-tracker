package nl.marisabel.backend.expenses.controller;

import lombok.Getter;
import lombok.Setter;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExpenseUpdateForm {

    private List<ExpenseEntity> expenses;

    // Default constructor
    public ExpenseUpdateForm() {
        this.expenses = new ArrayList<>();
    }

    // Getter and setter for expenses

    // Add a convenience method to add an expense to the list
    public void addExpense(ExpenseEntity expense) {
        this.expenses.add(expense);
    }
}