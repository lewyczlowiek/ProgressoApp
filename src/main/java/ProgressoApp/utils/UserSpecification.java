package ProgressoApp.utils;

import ProgressoApp.model.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification implements Specification<User> {

    private final SearchCriteria criteria;

    public UserSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<User> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {

        String key = criteria.getKey();
        Object value = criteria.getValue();
        String op = criteria.getOperation().toLowerCase();

        switch (op) {
            case "=":
                return builder.equal(root.get(key), value);

            case "like":
                return builder.like(
                        builder.lower(root.get(key)),
                        "%" + value.toString().toLowerCase() + "%"
                );

            case ">":
                return builder.greaterThan(root.get(key), (Comparable) value);

            case "<":
                return builder.lessThan(root.get(key), (Comparable) value);

            case ">=":
                return builder.greaterThanOrEqualTo(root.get(key), (Comparable) value);

            case "<=":
                return builder.lessThanOrEqualTo(root.get(key), (Comparable) value);

            case "!=":
            case "<>":
                return builder.notEqual(root.get(key), value);

            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }
    }
}
