package ru.oshokin.services;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import ru.oshokin.persist.entities.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Page<Product> applyFilter(Optional<String> nameFilter,
                              Optional<BigDecimal> minPrice,
                              Optional<BigDecimal> maxPrice,
                              Optional<Integer> page,
                              Integer size,
                              String sortField);

    List<Product> findAll(Specification<Product> spec);
    Optional<Product> findById(Long id);
    void save(Product product);
    void deleteById(Long id);

}