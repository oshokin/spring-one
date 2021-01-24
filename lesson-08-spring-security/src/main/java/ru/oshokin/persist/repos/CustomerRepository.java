package ru.oshokin.persist.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.oshokin.persist.entities.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    List<Customer> findCustomerByFirstNameLike(String name);
    List<Customer> findCustomerByLastNameLike(String name);
    Optional<Customer> findCustomersByEmail(String login);

}