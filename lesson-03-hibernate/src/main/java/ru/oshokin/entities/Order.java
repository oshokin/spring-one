package ru.oshokin.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @EntityField(presentation = "seq. №", skippedOnInsert = true, skippedOnUpdate = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EntityField(presentation = "customer")
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @EntityField(presentation = "date", skippedOnInsert = true)
    @Column
    private LocalDateTime date;

    @EntityField(presentation = "items", skippedOnSearch = true, skippedOnInsert = true, skippedOnUpdate = true)
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(Customer customer, List<OrderItem> items) {
        super();
        this.customer = customer;
        this.date = LocalDateTime.now();
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addOrderItem(Product product, BigDecimal price, Long quantity) {
        if (items == null) this.items = new ArrayList<>();
        items.add(new OrderItem(this, product, price, quantity));
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        if (items == null) this.items = new ArrayList<>();
        items.add(orderItem);
    }

    @Override
    public String toString() {
        return String.format("Order (id: %d, customer: %s, from %s, items: %d)",
                id,
                customer,
                date,
                (items != null ? items.size() : 0));
    }
}
