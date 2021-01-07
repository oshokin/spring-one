package ru.oshokin.persist.repo;

import org.springframework.data.jpa.domain.Specification;
import ru.oshokin.persist.entity.Product;

public class ProductSpecification {

    public static Specification<Product> nameLike(String name) {
        return (root, query, builder) -> builder.like(root.get("name"), "%" + name + "%");
    }
    // TODO добавить спецификации для условий согласно ДЗ

}
