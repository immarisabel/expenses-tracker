package nl.marisabel.frontend.savings;

import lombok.Getter;
import lombok.Setter;

import java.time.Month;
import java.time.Year;

@Getter
@Setter
public class SavingsModel {

 private Long goalId;
 private double amount;
 private String monthYear;

 }


