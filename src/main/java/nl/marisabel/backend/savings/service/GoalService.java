package nl.marisabel.backend.savings.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.repository.GoalRepository;
import org.hibernate.Hibernate;
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



 public void saveOrUpdate(GoalEntity goal){
  goalRepository.save(goal);
 }

 public List<GoalEntity> getAllGoals() {
  return goalRepository.findAll();
 }

 @Transactional
 public Optional<GoalEntity> getGoalById(Long id) {
  Optional<GoalEntity> goal = goalRepository.findById(id);
  goal.ifPresent(g -> Hibernate.initialize(g.getSavingsEntities()));
  return goal;
 }

 public void updateGoalState(GoalEntity goal, double amount) {
  log.info("Last amount of goal: " + goal.getLastAmount());

  goal.setLastAmount(goal.getLastAmount() + amount);
  if (goal.getLastAmount() >= goal.getMaxAmount()) {
   goal.setReached(true);
  }

  log.info("New goal amount: " + goal.getName() + " : " + goal.getLastAmount());
  log.info("Goal reached? " + goal.isReached());

  this.saveOrUpdate(goal);
 }

}
