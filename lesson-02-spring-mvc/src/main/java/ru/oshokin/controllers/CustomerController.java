package ru.oshokin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.oshokin.persist.Customer;
import ru.oshokin.persist.CustomerRepository;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public String indexCustomerPage(Model model) {
        logger.info("Customer page update");
        model.addAttribute("customers", customerRepository.findAll());
        return "customers_list";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Editing customer with id {}", id);
        model.addAttribute("customer", customerRepository.findById(id));
        return "customer_update";
    }

    @GetMapping("/new")
    public String newCustomer(Model model) {
        logger.info("Adding new customer");
        model.addAttribute("customer", new Customer());
        return "customer_create";
    }

    @PostMapping("/update")
    public String updateCustomer(Customer customer) {
        customerRepository.update(customer);
        return "redirect:/customer";
    }

    @PostMapping("/insert")
    public String insertCustomer(Customer customer) {
        customerRepository.insert(customer);
        return "redirect:/customer";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting customer with id {}", id);
        customerRepository.delete(id);
        return "redirect:/customer";
    }

}
