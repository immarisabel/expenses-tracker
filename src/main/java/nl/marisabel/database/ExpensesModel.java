package nl.marisabel.database;

import jakarta.persistence.*;
import lombok.Data;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Table(name = "expenses")
public class ExpensesModel {
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


}
