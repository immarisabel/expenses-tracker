package nl.marisabel.transactions.classes;

import jakarta.persistence.criteria.*;
import nl.marisabel.categories.classes.CategoryEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification implements Specification<TransactionEntity> {

 private final TransactionFilter filter;

 public TransactionSpecification(TransactionFilter filter) {
  this.filter = filter;
 }

 @Override
 public Predicate toPredicate(Root<TransactionEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
  List<Predicate> predicates = new ArrayList<>();

  if (filter.getSearchTerm() != null) {
   Predicate entityPredicate = builder.like(builder.lower(root.get("entity")), "%" + filter.getSearchTerm().toLowerCase() + "%");
   Predicate descriptionPredicate = builder.like(builder.lower(root.get("description")), "%" + filter.getSearchTerm().toLowerCase() + "%");
   predicates.add(builder.or(entityPredicate, descriptionPredicate));
  }

  if (filter.getMinAmount() != null) {
   predicates.add(builder.greaterThanOrEqualTo(root.get("amount"), filter.getMinAmount()));
  }

  if (filter.getMaxAmount() != null) {
   predicates.add(builder.lessThanOrEqualTo(root.get("amount"), filter.getMaxAmount()));
  }

  if (filter.getStartDate() != null) {
   predicates.add(builder.greaterThanOrEqualTo(root.get("date"), filter.getStartDate()));
  }

  if (filter.getEndDate() != null) {
   predicates.add(builder.lessThanOrEqualTo(root.get("date"), filter.getEndDate()));
  }

  if (filter.getCategoryId() != null) {
   Join<TransactionEntity, CategoryEntity> join = root.join("categories");
   predicates.add(builder.equal(join.get("id"), filter.getCategoryId()));
  }

  return builder.and(predicates.toArray(new Predicate[0]));
 }
}
