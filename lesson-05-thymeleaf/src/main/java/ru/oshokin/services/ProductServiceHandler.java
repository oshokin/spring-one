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
import java.util.Map;
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
    public Page<Product> applyFilter(Map<String, Object> parameters) {

        //через лямбду (функциональные интерфейсы Consumer, Function),
        //Java просит чтобы Specification<Product> spec был final,
        //красивее не сделать я думаю, или у меня не хватает опыта.
        //
        //PS: это все очень похоже на объект QueryBuilder в 1С,
        //он строит SQL запрос со списком заготавливаемых условий.

        String[] filterFields = {"nameFilter", "minPrice", "maxPrice"};
        Specification<Product> spec = Specification.where(null);

        for (String fieldName : filterFields) {
            Object fieldValue = parameters.get(fieldName);
            if (fieldValue == null) continue;
            if (fieldName.equals("nameFilter")) {
                String castedValue = (String) fieldValue;
                if (!castedValue.isEmpty()) spec = spec.and(ProductSpecification.nameLike(castedValue));
            }
            if (fieldName.equals("minPrice")) {
                spec = spec.and(ProductSpecification.minPrice((BigDecimal) fieldValue));
            }
            if (fieldName.equals("maxPrice")) {
                spec = spec.and(ProductSpecification.maxPrice((BigDecimal) fieldValue));
            }
        }

        String sortField = (String) parameters.get("sortField");
        String sortDirection = (String) parameters.get("sortDirection");
        int page = (int) parameters.get("page");
        int size = (int) parameters.get("size");

        Sort sort = (sortDirection.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
        return productRepository.findAll(spec, PageRequest.of(page - 1, size, sort));
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
