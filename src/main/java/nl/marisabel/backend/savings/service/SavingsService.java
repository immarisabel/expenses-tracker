package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.savings.entity.GoalEntity;
import nl.marisabel.backend.savings.entity.SavingsEntity;
import nl.marisabel.backend.savings.repository.SavingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingsService {
 private final SavingsRepository savingsRepository;

 public SavingsService(SavingsRepository savingsRepository) {
  this.savingsRepository = savingsRepository;
 }

 public SavingsEntity save(SavingsEntity savingsEntity) {
  return savingsRepository.save(savingsEntity);
 }

 public List<SavingsEntity> getAllSavings() {
  return savingsRepository.findAll();
 }
}
