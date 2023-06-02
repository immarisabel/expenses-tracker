package nl.marisabel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
 public static void main(String[] args) throws Exception {
//  CsvReader csvReader = new CsvReader();
//  csvReader.readFile(new File("src/main/resources/032020-042020.csv"));

  File fileLocation= new File("src/main/resources/032020-042020.csv");
  ExpensesService expensesService = new ExpensesService();
  expensesService.read(fileLocation);

 }



}