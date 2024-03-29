package nl.marisabel.savings.classes;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class GoalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private double maxAmount;
    private double lastAmount;
    private boolean reached;

    @OneToMany(mappedBy = "goal")
    private List<SavingsEntity> savingsEntities;

}


