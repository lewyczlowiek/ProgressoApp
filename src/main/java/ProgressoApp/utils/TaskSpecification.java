package ProgressoApp.utils;

import ProgressoApp.model.Task;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification implements Specification<Task> {

    private final SearchCriteria criteria;

    public TaskSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Task> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        String key = criteria.getKey();
        Object value = criteria.getValue();
        String op = criteria.getOperation().toLowerCase();

        switch (op) {
            case "=":
                if ("projectId".equalsIgnoreCase(key)) {
                    // jeśli chcesz filtrować po identyfikatorze projektu
                    return builder.equal(
                            root.join("project").get("projectId"),
                            value
                    );
                } else {
                    return builder.equal(root.get(key), value);
                }

            case "like":
                return builder.like(
                        builder.lower(root.get(key)),
                        "%" + value.toString().toLowerCase() + "%"
                );

            case ">":
                if ("dueDate".equalsIgnoreCase(key) || "taskOrder".equalsIgnoreCase(key)) {
                    return builder.greaterThan(
                            root.get(key),
                            (Comparable) value
                    );
                } else {
                    return builder.greaterThan(root.get(key), value.toString());
                }

            case "<":
                if ("dueDate".equalsIgnoreCase(key) || "taskOrder".equalsIgnoreCase(key)) {
                    return builder.lessThan(
                            root.get(key),
                            (Comparable) value
                    );
                } else {
                    return builder.lessThan(root.get(key), value.toString());
                }

            default:
                return null;
        }
    }
}
