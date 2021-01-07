package ru.oshokin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oshokin.persist.entities.Product;
import ru.oshokin.persist.repos.ProductRepository;
import ru.oshokin.persist.repos.ProductSpecification;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceHandler implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> applyFilter(Optional<String> nameFilter,
                                        Optional<BigDecimal> minPrice,
                                        Optional<BigDecimal> maxPrice,
                                        Optional<Integer> page,
                                        Integer size,
                                        String sortField) {

        Specification<Product> spec = Specification.where(null);

        if (nameFilter.isPresent() && !nameFilter.get().isEmpty()) {
            spec = spec.and(ProductSpecification.nameLike(nameFilter.get()));
        }

        if (minPrice.isPresent()) {
            spec = spec.and(ProductSpecification.minPrice(minPrice.get()));
        }

        if (maxPrice.isPresent() ) {
            spec = spec.and(ProductSpecification.maxPrice(maxPrice.get()));
        }

        Sort sort = Sort.by(sortField).ascending();
        return productRepository.findAll(spec, PageRequest.of(page.orElse(1) - 1, size, sort));
    }

    @Override
    public List<Product> findAll(Specification<Product> spec) {
        return productRepository.findAll(spec);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

}
