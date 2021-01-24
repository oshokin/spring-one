package ru.oshokin.services;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import ru.oshokin.persist.entities.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    Page<Product> applyFilter(Map<String, Object> parameters);
    List<Product> findAll(Specification<Product> spec);
    Optional<Product> findById(Long id);
    void save(Product product);
    void deleteById(Long id);

}