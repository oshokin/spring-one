package ru.oshokin.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @EntityField(presentation = "seq. №", skippedOnInsert = true, skippedOnUpdate = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EntityField(presentation = "order")
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @EntityField(presentation = "product")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @EntityField(presentation = "price")
    @Column(nullable = false)
    private BigDecimal price;

    @EntityField(presentation = "quantity")
    @Column(nullable = false)
    private Long quantity;

    @EntityField(presentation = "amount", skippedOnInsert = true, skippedOnUpdate = true)
    @Column(nullable = false)
    private BigDecimal amount;

    public OrderItem() {
    }

    public OrderItem(Order order, Product product, BigDecimal price, Long quantity) {
        this.order = order;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.amount = this.price.multiply(new BigDecimal(this.quantity));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("OrderItem (id: %d, order: %s; %s, %s * %d = %s)",
                id,
                order,
                product,
                price,
                quantity,
                amount);
    }
}
