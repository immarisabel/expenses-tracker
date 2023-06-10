package nl.marisabel.backend.transactions.controller;

import lombok.Getter;
import lombok.Setter;
import nl.marisabel.backend.transactions.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionUpdateForm {

    private List<TransactionEntity> transactions;
    private Long categoryId;


    // Default constructor
    public TransactionUpdateForm() {
        this.transactions = new ArrayList<>();
    }

    // Add a convenience method to add an transactions to the list
    public void addTransaction(TransactionEntity transactions) {
        this.transactions.add(transactions);
    }
}