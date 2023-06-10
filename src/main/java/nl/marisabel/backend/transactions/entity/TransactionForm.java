package nl.marisabel.backend.transactions.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionForm {

    private List<Long> selectedTransactionIds;
    private Long categoryId;

    public TransactionForm() {
        this.selectedTransactionIds = new ArrayList<>();
    }

}
