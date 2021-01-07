package ru.oshokin.persist.repo;

import org.springframework.data.jpa.domain.Specification;
import ru.oshokin.persist.entity.Customer;

public class CustomerSpecification {

    public static Specification<Customer> firstNameLike(String firstName) {
        return (root, query, builder) -> builder.like(root.get("firstName"), "%" + firstName + "%");
    }
    // TODO добавить спецификации для условий согласно ДЗ

}