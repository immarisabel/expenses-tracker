package nl.marisabel.backend.expenses.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExpenseFormDto {

    private List<Long> selectedExpenseIds;
    private Long categoryId;

    public ExpenseFormDto() {
        this.selectedExpenseIds = new ArrayList<>();
    }

}
