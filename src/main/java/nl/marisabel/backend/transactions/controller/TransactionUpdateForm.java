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


    public TransactionUpdateForm() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(TransactionEntity transactions) {
        this.transactions.add(transactions);
    }
}