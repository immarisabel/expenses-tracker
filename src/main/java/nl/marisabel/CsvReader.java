package nl.marisabel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CsvReader {


 public void readFile(File fileUpload) throws FileNotFoundException {
  Scanner sc = new Scanner(fileUpload);
  sc.useDelimiter(",");
  while (sc.hasNext())

  {
   System.out.print(sc.next());
  }

  sc.close();
 }
}
