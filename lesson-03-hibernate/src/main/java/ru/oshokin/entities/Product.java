package ru.oshokin.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @EntityField(presentation = "seq. №", skippedOnInsert = true, skippedOnUpdate = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EntityField(presentation = "name")
    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @EntityField(presentation = "description", skippedOnSearch = true)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @EntityField(presentation = "price")
    @Column(nullable = false)
    private BigDecimal price;

    @EntityField(presentation = "order items", skippedOnSearch = true, skippedOnInsert = true, skippedOnUpdate = true)
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public Product() {
        this.orderItems = new ArrayList<>();
    }

    public Product(Long id, String name, String description, BigDecimal price) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return String.format("Product (id: %d, name: %s, description: %s, price: %s, sold %d times)",
                id,
                name,
                description,
                price,
                (orderItems != null ? orderItems.size() : 0));
    }
}
