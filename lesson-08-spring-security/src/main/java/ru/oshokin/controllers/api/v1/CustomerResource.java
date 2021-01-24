package ru.oshokin.controllers.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oshokin.controllers.NotFoundException;
import ru.oshokin.persist.entities.Customer;
import ru.oshokin.persist.repos.CustomerRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/api/v1/customer")
@RestController
public class CustomerResource {

    private final CustomerRepository repository;

    @Autowired
    public CustomerResource(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/all")
    public List<Customer> findall(){
        return repository.findAll();
    }

    @GetMapping(path = "/{id}/id")
    public Customer findById(@PathVariable("id") long id) {
        return repository.findById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        repository.save(customer);
        return customer;
    }

    @PutMapping
    public Customer updateCustomer(@RequestBody Customer customer) {
        if (!repository.existsById(customer.getId())) throw new NotFoundException();
        repository.save(customer);
        return customer;
    }

    @DeleteMapping(path = "/{id}/id")
    public void deleteCustomer(@PathVariable("id") long id) {
        repository.deleteById(id);
    }

    @ExceptionHandler
    public ResponseEntity<String> notFoundExceptionHandler(NotFoundException e) {
        return new ResponseEntity<String>("Customer not found", HttpStatus.NOT_FOUND);
    }

}