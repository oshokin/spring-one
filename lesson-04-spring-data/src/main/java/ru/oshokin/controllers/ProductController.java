package ru.oshokin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.oshokin.persist.entity.Product;
import ru.oshokin.persist.repo.ProductRepository;
import ru.oshokin.persist.repo.ProductSpecification;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String indexProductPage(Model model, @RequestParam(name = "nameFilter", required = false) String nameFilter) {
        logger.info("Product page update");
        Specification<Product> spec = Specification.where(null);
        if (nameFilter != null && !nameFilter.isEmpty()) {
            spec = spec.and(ProductSpecification.nameLike(nameFilter));
        }
        // TODO добавить обработку параметров формы
        model.addAttribute("products", productRepository.findAll(spec));
        return "products_list";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Editing product with id {}", id);
        model.addAttribute("product", productRepository.findById(id));
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
        productRepository.save(product);
        return "redirect:/product";
    }

    @PostMapping("/insert")
    public String insertProduct(Product product) {
        productRepository.save(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting product with id {}", id);
        productRepository.deleteById(id);
        return "redirect:/product";
    }

}
