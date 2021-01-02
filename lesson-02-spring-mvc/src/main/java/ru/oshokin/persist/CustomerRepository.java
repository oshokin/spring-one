package ru.oshokin.persist;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerRepository {

    private final AtomicLong identity = new AtomicLong(0);

    private final Map<Long, Customer> identityMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        insert(new Customer(null, "Oleg", "Shokin", "ur_mommas_best_friend@ya.ru"));
        insert(new Customer(null, "Jordi", "Rey Martinez", "jordi_rey@gmail.com"));
        insert(new Customer(null, "Claudia", "Zambrano", "clau90210@hotmail.com"));
        insert(new Customer(null, "Gustavo", "Salazar Quispe", "gustavito_tu_rey@hotmail.com"));
    }

    public void insert(Customer product) {
        product.setId(identity.incrementAndGet());
        identityMap.put(product.getId(), product);
    }

    public void update(Customer product) {
        identityMap.put(product.getId(), product);
    }

    public void delete(long id) {
        identityMap.remove(id);
    }

    public Customer findById(long id) {
        return identityMap.get(id);
    }

    public List<Customer> findAll() {
        return new ArrayList<>(identityMap.values());
    }

}