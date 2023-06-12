package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.repository.GoalRepository;
import nl.marisabel.backend.savings.repository.SavingsRepository;
import org.springframework.stereotype.Service;

@Service
public class GoalService {
 private final GoalRepository goalRepository;

 public GoalService(GoalRepository goalRepository) {
  this.goalRepository = goalRepository;
 }


 public void saveNewGoal(GoalEntity goal){
  goalRepository.save(goal);
 }
}
