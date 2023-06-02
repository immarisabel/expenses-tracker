package nl.marisabel;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;


@Component
@Log4j2
public class ExpensesCvsReaderING {

 private final ExpenseRepository expenseRepository;

 public ExpensesCvsReaderING(ExpenseRepository expenseRepository) {
  this.expenseRepository = expenseRepository;
 }

 public void read(InputStream file) throws Exception {
  CSVReader csvReader = null;

  try {
   csvReader = new CSVReaderBuilder(new InputStreamReader(file))
           .withCSVParser(new CSVParserBuilder()
                   .withSeparator(';')
                   .build())
           .build();

   String[] headers = csvReader.readNext(); // Skip header row
   if (headers == null) {
    log.error("No header row found in the CSV file");
    return;
   }

   int currentLine = 2; // Start from line 2 (after the header row)

   String[] record;
   while ((record = csvReader.readNext()) != null) {
    try {
     ExpensesModel expense = new ExpensesModel();
     expense.setDate(record[0]);
     expense.setEntity(record[1]);
     expense.setCreditOrDebit(record[5]);
     expense.setAmount(record[6]);
     expense.setDescription(record[8]);
     expenseRepository.save(expense);

     log.info("Expense added: {}", expense);
    } catch (Exception e) {
     log.error("Error parsing record at line {}: {}", currentLine, e.getMessage());
    }

    currentLine++;
   }
  } finally {
   if (csvReader != null) {
    csvReader.close();
   }
  }
 }


}
