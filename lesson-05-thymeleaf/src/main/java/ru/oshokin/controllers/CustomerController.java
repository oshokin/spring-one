package ru.oshokin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.oshokin.persist.entities.Customer;
import ru.oshokin.persist.repos.CustomerRepository;
import ru.oshokin.persist.repos.CustomerSpecification;

import javax.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public String indexCustomerPage(Model model, @RequestParam(name = "firstNameFilter", required = false) String firstNameFilter) {
        logger.info("Customer page update");
        Specification<Customer> spec = Specification.where(null);
        if (firstNameFilter != null && !firstNameFilter.isEmpty()) {
            spec = spec.and(CustomerSpecification.firstNameLike(firstNameFilter));
        }
        model.addAttribute("customers", customerRepository.findAll(spec));
        return "customers_list";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Editing customer with id {}", id);
        model.addAttribute("customer", customerRepository.findById(id).orElseThrow(() -> new NotFoundException()));
        return "customer_update";
    }

    @GetMapping("/new")
    public String newCustomer(Model model) {
        logger.info("Adding new customer");
        model.addAttribute("customer", new Customer());
        return "customer_create";
    }

    @PostMapping("/update")
    public String updateCustomer(@Valid Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "customer_update";
        customerRepository.save(customer);
        return "redirect:/customer";
    }

    @PostMapping("/insert")
    public String insertCustomer(@Valid Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "customer_create";
        customerRepository.save(customer);
        return "redirect:/customer";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting customer with id {}", id);
        customerRepository.deleteById(id);
        return "redirect:/customer";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(NotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("page_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

}
