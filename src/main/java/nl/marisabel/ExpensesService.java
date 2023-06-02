package nl.marisabel;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;


@Service
public class ExpensesService {


 public void read(File file) throws Exception {

  String [] record;
  CSVReader csvReader = null;

  try {

   csvReader = new CSVReaderBuilder(new FileReader(file))
           .withCSVParser(new CSVParserBuilder()
                   .withSeparator(';')
                   .build())
           .build();

   int currentLine = 1;

   while ((record = csvReader.readNext()) != null) {

    System.out.println(
            String.format(
                    "[0]: %s [1]: %s [2]: %s [5]: %s [6]: %s [8]: %s",
                    record[0],
                    record[1],
                    record[2],
                    record[5],
                    record[6],
                    record[8]
            )
    );

    currentLine++;
   }
  } finally {
   if (csvReader != null) {
    csvReader.close();
   }
  }
 }
 }



