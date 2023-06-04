package nl.marisabel.util;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.expenses.repository.ExpenseRepository;
import nl.marisabel.backend.expenses.entity.ExpenseEntity;
import nl.marisabel.frontend.charts.ExpenseUploadResult;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@Log4j2
public class ExpensesCvsReaderING {

    private final ExpenseRepository expenseRepository;
    private final ExpenseUploadResult result;
    private int duplicateCount = 0;
    private int nonDuplicateCount = 0;

    public ExpensesCvsReaderING(ExpenseRepository expenseRepository, ExpenseUploadResult result) {
        this.expenseRepository = expenseRepository;
        this.result = result;
    }

    public ExpenseUploadResult read(InputStream file) throws Exception {

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

            int currentLine = 2; // Start from line 2 (after the header row)

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                try {
                    String description = record[8];

                    // Check for duplicates before adding to the database
                    boolean isDuplicate = expenseRepository.existsByDescriptionIgnoreCase(description);

                    if (!isDuplicate) {
                        ExpenseEntity expense = new ExpenseEntity();
                        expense.setDate(record[0]);  // This now parses the date string to LocalDate
                        expense.setEntity(record[1]);
                        expense.setCreditOrDebit(record[5]);
                        expense.setAmount(record[6]);
                        expense.setDescription(description);
                        expenseRepository.save(expense);

                        nonDuplicateCount++;  // Increase non-duplicate counter

                        log.info("Expense added: {}", expense);
                    } else {
                        duplicateCount++;  // Increase duplicate counter

                        log.info("Duplicate expense found at line {}: {}", currentLine, description);
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
