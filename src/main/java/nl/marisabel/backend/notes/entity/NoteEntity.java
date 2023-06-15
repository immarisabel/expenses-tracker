package nl.marisabel.backend.notes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NoteEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 public Long id;
 @Column(columnDefinition = "TEXT")
 public String note;
}
