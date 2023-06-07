package nl.marisabel.backend.savings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GoalModel {
    private String name;
    private double max;
    private double value;


}