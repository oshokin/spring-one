package ru.oshokin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.oshokin.persist.entities.Product;
import ru.oshokin.services.ProductService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String indexProductPage(Model model, @RequestParam Map<String, String> parameters) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.error("SHOA BEGIN parameters: {}", LocalDateTime.now().format(formatter));
        parameters.forEach((k, v) -> {
            logger.error("SHOA: key is \"{}\", value is \"{}\"", k, v);
        });
        logger.error("SHOA END parameters: {}", LocalDateTime.now().format(formatter));
        final int defaultParametersCount = 4;
        Map<String, Object> parsedParameters = new HashMap<>(parameters.size() + defaultParametersCount);

        parsedParameters.put("nameFilter", parameters.get("nameFilter"));
        parsedParameters.put("minPrice", castBigDecimal(parameters.get("minPrice")));
        parsedParameters.put("maxPrice", castBigDecimal(parameters.get("maxPrice")));

        parsedParameters.put("page", getIntegerOrDefault(parameters.get("page"), 1));
        parsedParameters.put("size", getIntegerOrDefault(parameters.get("size"), DEFAULT_PAGE_SIZE));
        parsedParameters.put("sortField", parameters.getOrDefault("sortField", "id"));
        parsedParameters.put("sortDirection", parameters.getOrDefault("sortDirection", "asc"));

        logger.error("SHOA BEGIN parsedParameters: {}", LocalDateTime.now().format(formatter));
        parsedParameters.forEach((k, v) -> {
            logger.error("SHOA: key is \"{}\", value is \"{}\"", k, v);
        });
        logger.error("SHOA END parsedParameters: {}", LocalDateTime.now().format(formatter));

        model.addAttribute("selectedPageSize", parsedParameters.get("size"));
        model.addAttribute("sortValue", parsedParameters.get("sortField"));
        model.addAttribute("sortDirection", parsedParameters.get("sortDirection"));

        //полиморфизм в действии
        model.addAttribute("products", productService.applyFilter(parsedParameters));

        return "products_list";
    }

    private int getIntegerOrDefault(String value, int defaultValue) {
        int funcResult;
        try {
            funcResult = Integer.parseInt(value);
        } catch(NumberFormatException e) {
            funcResult = defaultValue;
        }
        return funcResult;
    }

    private BigDecimal castBigDecimal(String value) {
        BigDecimal funcResult = null;
        if ((value != null) && !value.isEmpty()) {
            try {
                funcResult = new BigDecimal(value);
            } catch (NumberFormatException e) {
                funcResult = null;
            }
        }
        return funcResult;
    }

    //С каждым добавлением еще одного параметра в метод,
    //1 маленький Макконнел (вместе с Робертом Мартином) тихо плачет в стороне :)
    //@GetMapping
    //public String indexProductPage(Model model,
    //                               @RequestParam(name = "nameFilter") Optional<String> nameFilter,
    //                               @RequestParam(name = "minPrice") Optional<BigDecimal> minPrice,
    //                               @RequestParam(name = "maxPrice") Optional<BigDecimal> maxPrice,
    //                               @RequestParam(name = "page") Optional<Integer> page,
    //                               @RequestParam(name = "size") Optional<Integer> size,
    //                               @RequestParam(name = "sortField") Optional<String> sortField) {
    //    logger.info("Product page update");
    //    int pageSize = size.orElse(DEFAULT_PAGE_SIZE);
    //   String sortValue = sortField.orElse("id");
    //
    //
    //
    //   model.addAttribute("selectedPageSize", pageSize);
    //  model.addAttribute("sortValue", sortValue);
    //  model.addAttribute("products", productService.applyFilter(nameFilter, minPrice, maxPrice, page, pageSize, sortValue));
    //
    //   return "products_list";
    //}

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Editing product with id {}", id);
        model.addAttribute("product", productService.findById(id).orElseThrow(() -> new NotFoundException()));
        return "product_update";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        logger.info("Adding new product");
        model.addAttribute("product", new Product());
        return "product_create";
    }

    @PostMapping("/update")
    public String updateProduct(@Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "product_update";
        productService.save(product);
        return "redirect:/product";
    }

    @PostMapping("/insert")
    public String insertProduct(@Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "product_create";
        productService.save(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting product with id {}", id);
        productService.deleteById(id);
        return "redirect:/product";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(NotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("page_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

}