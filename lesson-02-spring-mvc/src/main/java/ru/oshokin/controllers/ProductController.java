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
import ru.oshokin.persist.Product;
import ru.oshokin.persist.ProductRepository;

@Controller
@RequestMapping("/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String indexProductPage(Model model) {
        logger.info("Product page update");
        model.addAttribute("products", productRepository.findAll());
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
        productRepository.update(product);
        return "redirect:/product";
    }

    @PostMapping("/insert")
    public String insertProduct(Product product) {
        productRepository.insert(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Deleting product with id {}", id);
        productRepository.delete(id);
        return "redirect:/product";
    }

}
