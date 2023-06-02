package nl.marisabel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.opencsv.bean.CsvBindByName;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Expense {
 @CsvBindByName(column = "Date")
 private String date;

 @CsvBindByName(column = "Name / Description")
 private String nameDescription;

 @CsvBindByName(column = "Account")
 private String account;

 @CsvBindByName(column = "Counterparty")
 private String counterparty;

 @CsvBindByName(column = "Code")
 private String code;

 @CsvBindByName(column = "Debit/credit")
 private String debitCredit;

 @CsvBindByName(column = "Amount (EUR)")
 private double amount;

 @CsvBindByName(column = "Transaction type")
 private String transactionType;

 @CsvBindByName(column = "Notifications")
 private String notifications;

 @CsvBindByName(column = "Resulting balance")
 private double resultingBalance;

 @CsvBindByName(column = "Tag")
 private String tag;


}
