package ru.oshokin.controllers.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.oshokin.controllers.CommonUtils;
import ru.oshokin.controllers.NotFoundException;
import ru.oshokin.persist.entities.Product;
import ru.oshokin.services.ProductService;

import javax.validation.Valid;
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
        final int defaultParametersCount = 4;
        Map<String, Object> parsedParameters = new HashMap<>(parameters.size() + defaultParametersCount);

        parsedParameters.put("nameFilter", parameters.get("nameFilter"));
        parsedParameters.put("minPrice", CommonUtils.castBigDecimal(parameters.get("minPrice")));
        parsedParameters.put("maxPrice", CommonUtils.castBigDecimal(parameters.get("maxPrice")));

        parsedParameters.put("page", CommonUtils.getIntegerOrDefault(parameters.get("page"), 1));
        parsedParameters.put("size", CommonUtils.getIntegerOrDefault(parameters.get("size"), DEFAULT_PAGE_SIZE));
        parsedParameters.put("sortField", parameters.getOrDefault("sortField", "id"));
        parsedParameters.put("sortOrder", parameters.getOrDefault("sortOrder", "asc"));

        model.addAttribute("sizeAttribute", parsedParameters.get("size"));
        model.addAttribute("sortFieldAttribute", parsedParameters.get("sortField"));
        model.addAttribute("sortOrderAttribute", parsedParameters.get("sortOrder"));

        model.addAttribute("products", productService.applyFilter(parsedParameters));

        return "products_list";
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