package nl.marisabel.backend.transactions.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.marisabel.backend.categories.entity.CategoryEntity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "categories")  // excluding the relationship field from toString()
@Table(name = "expenses")
public class ExpenseEntity {
 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 private Long id;
 private LocalDate date;
 private String entity;
 private String creditOrDebit;
 private double amount;
 @Column(columnDefinition = "TEXT")
 private String description;

 public void setAmount(String amount) throws ParseException {
  DecimalFormatSymbols symbols = new DecimalFormatSymbols();
  symbols.setDecimalSeparator(',');
  DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
  this.amount = decimalFormat.parse(amount).doubleValue();
 }

 public void setDate(String date) {
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  this.date = LocalDate.parse(date, formatter);
 }

 @ManyToMany(fetch = FetchType.EAGER,
         cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
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
