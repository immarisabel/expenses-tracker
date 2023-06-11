package nl.marisabel.backend.savings.service;

import nl.marisabel.backend.savings.repository.SavingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SavingsService {
 private final SavingsRepository savingsRepository;

 public SavingsService(SavingsRepository savingsRepository) {
  this.savingsRepository = savingsRepository;
 }


}
