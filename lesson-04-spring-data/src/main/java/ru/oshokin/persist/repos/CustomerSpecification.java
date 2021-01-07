package ru.oshokin.persist.repos;

import org.springframework.data.jpa.domain.Specification;
import ru.oshokin.persist.entities.Customer;

public class CustomerSpecification {

    public static Specification<Customer> firstNameLike(String firstName) {
        return (root, query, builder) -> builder.like(root.get("firstName"), "%" + firstName + "%");
    }

}