package ru.oshokin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oshokin.persist.entities.Customer;
import ru.oshokin.persist.repos.CustomerRepository;
import ru.oshokin.persist.repos.CustomerSpecification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerServiceHandler implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<Customer> applyFilter(Map<String, Object> parameters) {

        String[] filterFields = {"firstNameFilter", "lastNameFilter"};
        Specification<Customer> spec = Specification.where(null);

        for (String fieldName : filterFields) {
            Object fieldValue = parameters.get(fieldName);
            if (fieldValue == null) continue;
            if (fieldName.equals("firstNameFilter")) {
                String castedValue = (String) fieldValue;
                if (!castedValue.isEmpty()) spec = spec.and(CustomerSpecification.firstNameLike(castedValue));
            }
            if (fieldName.equals("lastNameFilter")) {
                String castedValue = (String) fieldValue;
                if (!castedValue.isEmpty()) spec = spec.and(CustomerSpecification.lastNameLike(castedValue));
            }
        }

        String sortField = (String) parameters.get("sortField");
        String sortOrder = (String) parameters.get("sortOrder");
        int page = (int) parameters.get("page");
        int size = (int) parameters.get("size");

        return customerRepository.findAll(spec,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.fromString(sortOrder), sortField)));
    }

    @Override
    public List<Customer> findAll(Specification<Customer> spec) {
        return customerRepository.findAll(spec);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

}