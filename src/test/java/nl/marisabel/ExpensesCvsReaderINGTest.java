package nl.marisabel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.File;


@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class ExpensesCvsReaderINGTest {

 @Mock
 private ExpenseRepository expenseRepository;

 @Test
 public void testRead() throws Exception {
  ExpensesCvsReaderING expensesCvsReader = new ExpensesCvsReaderING(expenseRepository);

  // Provide the path to a test CSV file
  File testFile = new File("src/main/resources/032020-042020.csv");

  expensesCvsReader.read(testFile);

 }
}