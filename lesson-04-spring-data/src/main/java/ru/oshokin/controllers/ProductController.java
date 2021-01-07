package ru.oshokin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.oshokin.persist.entities.Product;
import ru.oshokin.services.ProductService;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String indexProductPage(Model model,
                                   @RequestParam(name = "nameFilter") Optional<String> nameFilter,
                                   @RequestParam(name = "minPrice") Optional<BigDecimal> minPrice,
                                   @RequestParam(name = "maxPrice") Optional<BigDecimal> maxPrice,
                                   @RequestParam(name = "page") Optional<Integer> page,
                                   @RequestParam(name = "size") Optional<Integer> size,
                                   @RequestParam(name = "sortField") Optional<String> sortField) {
        logger.info("Product page update");
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);
        String sortValue = sortField.orElse("id");
        model.addAttribute("selectedPageSize", pageSize);
        model.addAttribute("sortValue", sortValue);
        model.addAttribute("products", productService.applyFilter(nameFilter, minPrice, maxPrice, page, pageSize, sortValue));

        return "products_list";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Editing product with id {}", id);
        model.addAttribute("product", productService.findById(id));
        return "product_update";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        logger.info("Adding new product");
        model.addAttribute("product", new Product());
        return "product_create";
    }

    @PostMapping("/update")
    public String updateProduct(Product product) {
        productService.save(product);
        return "redirect:/product";
    }

    @PostMapping("/insert")
    public String insertProduct(Product product) {
        productService.save(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting product with id {}", id);
        productService.deleteById(id);
        return "redirect:/product";
    }

}