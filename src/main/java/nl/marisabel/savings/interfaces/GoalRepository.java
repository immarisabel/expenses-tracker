package nl.marisabel.savings.interfaces;

import nl.marisabel.savings.classes.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {
}
