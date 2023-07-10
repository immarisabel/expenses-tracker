package nl.marisabel.backend.savings.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import nl.marisabel.backend.error.ResourceNotFoundException;
import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.repository.GoalRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * <h2>GOAL SERVICE</h2>
 * This class provides methods to save/update goals, retrieve all goals, retrieve a goal by ID,
 * update the state of a goal, and handle related operations.
 *
 * @Service - Indicates that this class is a service component.
 * @Log4j2 - Enables logging capabilities using Log4j2.
 */
@Log4j2
@Service
public class GoalServiceImp implements GoalService {

 private final GoalRepository goalRepository;

 public GoalServiceImp(GoalRepository goalRepository) {
  this.goalRepository = goalRepository;
 }


 /**
  * Saves or updates the given goal.
  * @param goal The GoalEntity object to be saved or updated.
  */
 public void saveOrUpdate(GoalEntity goal) {
  goalRepository.save(goal);
 }

 /**
  * Retrieves all goals.
  * @return A List of GoalEntity objects representing all goals.
  */
 public List<GoalEntity> getAllGoals() {
  return goalRepository.findAll();
 }

 /**
  * Retrieves a goal by its ID, and initializes its savings entities if present.
  * @param id The ID of the goal to retrieve.
  * @return An Optional object containing the retrieved GoalEntity if found, or empty if not found.
  */
 @Transactional
 public Optional<GoalEntity> getGoalById(Long id) {
  Optional<GoalEntity> goal = goalRepository.findById(id);
  goal.ifPresent(g -> Hibernate.initialize(g.getSavingsEntities()));
  return goal;
 }

 /**
  * Updates the state of the given goal based on the provided amount.
  * It logs the previous and new goal amount, and whether the goal is reached.
  * @param goal   The GoalEntity object to update the state for.
  * @param amount The amount to add to the goal's last amount.
  */
 public void updateGoalState(GoalEntity goal, double amount) {
  log.info("Last amount of goal: " + goal.getLastAmount());

  goal.setLastAmount(goal.getLastAmount() + amount);
  if (goal.getLastAmount() >= goal.getMaxAmount()) {
   goal.setReached(true);
   goalRepository.save(goal);
  }

  log.info("New goal amount: " + goal.getName() + " : " + goal.getLastAmount());
  log.info("Goal reached? " + goal.isReached());

  this.saveOrUpdate(goal);
 }


 /**
  * Handling exception if goal ID is not found
  * @param goalId
  * @return goal object, if not, throw exception
  */
 public GoalEntity getGoalByIdAndHandleException(Long goalId) {
  return goalRepository.findById(goalId)
          .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
 }




}

