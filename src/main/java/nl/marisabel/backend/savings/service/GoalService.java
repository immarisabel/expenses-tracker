package nl.marisabel.backend.savings.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.categories.entity.CategoryEntity;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.repository.GoalRepository;
import nl.marisabel.backend.savings.repository.SavingsRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class GoalService {
 private final GoalRepository goalRepository;

 public GoalService(GoalRepository goalRepository) {
  this.goalRepository = goalRepository;
 }



 public void saveNewGoal(GoalEntity goal){
  goalRepository.save(goal);
 }

 public List<GoalEntity> getAllGoals() {
  return goalRepository.findAll();
 }

 public Page<GoalEntity> getAllGoalsPageable(Pageable pageable) {
  return goalRepository.findAll(pageable);
 }

 @Transactional
 public Optional<GoalEntity> getGoalById(Long id) {
  Optional<GoalEntity> goal = goalRepository.findById(id);
  goal.ifPresent(g -> Hibernate.initialize(g.getSavingsEntities()));
  return goal;
 }
}
