package nl.marisabel.transactions.classes;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionFilter {
 private String searchTerm;
 private Double minAmount;
 private Double maxAmount;
 private LocalDate startDate;
 private LocalDate endDate;
 private Long categoryId;

}
