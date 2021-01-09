package ru.oshokin.services;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import ru.oshokin.persist.entities.Customer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomerService {

    Page<Customer> applyFilter(Map<String, Object> parameters);
    List<Customer> findAll(Specification<Customer> spec);
    Optional<Customer> findById(Long id);
    void save(Customer customer);
    void deleteById(Long id);

}