package ProgressoApp.utils;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import ProgressoApp.model.Project;
import ProgressoApp.utils.SearchCriteria;

public class ProjectSpecification implements Specification<Project> {

  private final SearchCriteria criteria;

  public ProjectSpecification(SearchCriteria criteria) {
    this.criteria = criteria;
  }

  @Override
  public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    if (criteria.getValue() == null) {
      return cb.conjunction();
    }

    if (root.get(criteria.getKey()).getJavaType() == String.class) {
      return cb.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
    }

    return cb.equal(root.get(criteria.getKey()), criteria.getValue());
  }
}
