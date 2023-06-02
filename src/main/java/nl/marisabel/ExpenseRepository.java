package nl.marisabel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpensesModel, Long> {

 List<ExpensesModel> findByDescriptionContainingIgnoreCaseOrEntityContainingIgnoreCase(String descriptionKeyword, String entityKeyword);

}