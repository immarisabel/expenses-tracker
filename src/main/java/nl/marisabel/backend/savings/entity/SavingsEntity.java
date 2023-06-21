package nl.marisabel.backend.savings.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Month;
import java.time.Year;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SavingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double amount;
    private Month savingsMonth;
    private Year savingYear;
    private String monthYear;


    @ManyToOne
    @JoinColumn(name = "goal_id")
    private GoalEntity goal;

}
