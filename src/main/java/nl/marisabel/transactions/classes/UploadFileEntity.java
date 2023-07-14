package nl.marisabel.transactions.classes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 private Long id;

 private String fileName;
 private LocalDate signatureDate = LocalDate.now();
 private int numberOfTransactions;
}
