package ru.oshokin.controllers.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.oshokin.controllers.CommonUtils;
import ru.oshokin.controllers.NotFoundException;
import ru.oshokin.persist.entities.Customer;
import ru.oshokin.persist.repos.RoleRepository;
import ru.oshokin.services.CustomerService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final CustomerService customerService;

    private final RoleRepository roleRepository;

    @Autowired
    public CustomerController(CustomerService customerService, RoleRepository roleRepository) {
        this.customerService = customerService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String indexCustomerPage(Model model, @RequestParam Map<String, String> parameters) {
        final int defaultParametersCount = 4;
        Map<String, Object> parsedParameters = new HashMap<>(parameters.size() + defaultParametersCount);

        parsedParameters.put("firstNameFilter", parameters.get("firstNameFilter"));
        parsedParameters.put("lastNameFilter", parameters.get("lastNameFilter"));

        parsedParameters.put("page", CommonUtils.getIntegerOrDefault(parameters.get("page"), 1));
        parsedParameters.put("size", CommonUtils.getIntegerOrDefault(parameters.get("size"), DEFAULT_PAGE_SIZE));
        parsedParameters.put("sortField", parameters.getOrDefault("sortField", "id"));
        parsedParameters.put("sortOrder", parameters.getOrDefault("sortOrder", "asc"));

        model.addAttribute("sizeAttribute", parsedParameters.get("size"));
        model.addAttribute("sortFieldAttribute", parsedParameters.get("sortField"));
        model.addAttribute("sortOrderAttribute", parsedParameters.get("sortOrder"));

        model.addAttribute("customers", customerService.applyFilter(parsedParameters));

        return "customers_list";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Editing customer with id {}", id);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("customer", customerService.findById(id).orElseThrow(() -> new NotFoundException()));
        return "customer_update";
    }

    @Secured({"ROLE_SUPERADMIN"})
    @GetMapping("/new")
    public String newCustomer(Model model) {
        logger.info("Adding new customer");
        model.addAttribute("customer", new Customer());
        return "customer_create";
    }

    @Secured({"ROLE_SUPERADMIN"})
    @PostMapping("/update")
    public String updateCustomer(@Valid Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "customer_update";
        customerService.save(customer);
        return "redirect:/customer";
    }

    @Secured({"ROLE_SUPERADMIN"})
    @PostMapping("/insert")
    public String insertCustomer(@Valid Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "customer_create";
        customerService.save(customer);
        return "redirect:/customer";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting customer with id {}", id);
        customerService.deleteById(id);
        return "redirect:/customer";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(NotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("page_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

}