package nl.marisabel.util;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.AutoCategoryEntity;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.categories.repository.AutoCategoryRepository;
import nl.marisabel.backend.transactions.repository.TransactionRepository;
import nl.marisabel.backend.transactions.entity.TransactionEntity;
import nl.marisabel.frontend.upload.TransactionUploadResult;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Log4j2
public class TransactionsCVSReaderING {
 private final TransactionRepository transactionRepository;
 private final AutoCategoryRepository autoCategoryRepository;
 private final TransactionUploadResult result;
 private int duplicateCount = 0;
 private int nonDuplicateCount = 0;

 public TransactionsCVSReaderING(TransactionRepository transactionRepository, AutoCategoryRepository autoCategoryRepository, TransactionUploadResult result) {
  this.transactionRepository = transactionRepository;
  this.autoCategoryRepository = autoCategoryRepository;
  this.result = result;
 }

 public TransactionUploadResult read(InputStream file) throws Exception {
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
    return null;
   }
   DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
   symbols.setDecimalSeparator(',');
   symbols.setGroupingSeparator('.');
   DecimalFormat decimalFormat = new DecimalFormat("0.00", symbols);

   int currentLine = 2; // Start from line 2 (after the header row)

   String[] record;
   while ((record = csvReader.readNext()) != null) {
    try {

     // basic columns reading
     String description = record[8];
     String entity = record[1];
     String creditOrDebit = record[5];
     double amount = decimalFormat.parse(record[6]).doubleValue();
     String strDate = record[0];
     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
     LocalDate date = LocalDate.parse(strDate, formatter);


//// check for categories matching the entity name and description
//     String autoCategoryName = "";
//
//     List<String> queriesToCheck = new ArrayList<>();
//
//     List<String> entityWords = Arrays.asList(entity.split("\\s+"));
//     queriesToCheck.addAll(entityWords);
//     List<String> descriptionWords = Arrays.asList(description.split("\\s+"));
//     queriesToCheck.addAll(descriptionWords);
//
//     for (String query : queriesToCheck) {
//      List<AutoCategoryEntity> autoCategory = autoCategoryRepository.findByQuery(query);
//      if (autoCategory != null) {
//       log.info(autoCategory.size() + " | " + autoCategory.get(0).getCategory());
//       autoCategoryName = autoCategory.get(0).getCategory();
//       break;
//      }
//     }
//
//     CategoryEntity categoryEntity = null;
//     if (!autoCategoryName.isEmpty()) {
//      categoryEntity = new CategoryEntity();
//      categoryEntity.setCategory(autoCategoryName);
//     }


     // Check for duplicates before adding to the database
     boolean isDuplicate = transactionRepository.transactionExists(
             LocalDate.parse(record[0], DateTimeFormatter.ofPattern("yyyyMMdd")),
             entity,
             creditOrDebit,
             amount,
             description);
     if (!isDuplicate) {

// testing if autocategorizing is doable
      String myCat = autoCategoryRepository.findByQuery("albert heijn").get(0).getCategory();
      CategoryEntity categoryEntity = new CategoryEntity();
      categoryEntity.setCategory(myCat);

// save transactions
      TransactionEntity transaction = new TransactionEntity();
      Set<CategoryEntity> categories = new HashSet<>();
      categories.add(categoryEntity);
      transaction.setCategories(categories);

      transaction.setDate(date);
      transaction.setEntity(entity);
      transaction.setCreditOrDebit(creditOrDebit);
      transaction.setAmount(amount);
      transaction.setDescription(description);
      transactionRepository.save(transaction);
      nonDuplicateCount++; // Increase non-duplicate counter
      log.info("transaction added: {}", transaction);
     } else {
      duplicateCount++; // Increase duplicate counter
      log.info("Duplicate transaction found at line {}: {}", currentLine, description);
     }
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
  result.setNonDuplicates(nonDuplicateCount);
  result.setDuplicates(duplicateCount);
  // Log the duplicate and non-duplicate counts
  log.info("Total non-duplicate records: {}", nonDuplicateCount);
  log.info("Total duplicate records: {}", duplicateCount);
  return result;
 }


}

// after uploading, display the items uploaded
// check transaction data last object.
// start new view form the next new object after uploading only!
// this is to check the categories if they are ok, or if I need to update them