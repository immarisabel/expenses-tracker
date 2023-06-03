package nl.marisabel.backend.expenses.entity;

import jakarta.persistence.*;
import lombok.Data;
import nl.marisabel.backend.categories.entity.CategoryEntity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "expenses")
public class ExpenseEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 private Long id;
 private String date;
 private String entity;
 private String creditOrDebit;
 private double amount;
 private String description;

 public void setAmount(String amount) throws ParseException {
  DecimalFormatSymbols symbols = new DecimalFormatSymbols();
  symbols.setDecimalSeparator(',');
  DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
  this.amount = decimalFormat.parse(amount).doubleValue();
 }


 @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
 @JoinTable(
         name = "category_expenses",
         joinColumns = @JoinColumn(name = "expense_id"),
         inverseJoinColumns = @JoinColumn(name = "category_id"))
 private Set<CategoryEntity> categories = new HashSet<>();

 public void addCategory(CategoryEntity category) {
  categories.add(category);
  category.getExpenses().add(this);
 }

 public void removeCategory(CategoryEntity category) {
  categories.remove(category);
  category.getExpenses().remove(this);
 }
}
