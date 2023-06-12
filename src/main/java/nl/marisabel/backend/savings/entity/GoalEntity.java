package nl.marisabel.backend.savings.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
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


