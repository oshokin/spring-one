package ru.oshokin.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @EntityField(presentation = "seq. №", skippedOnInsert = true, skippedOnUpdate = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EntityField(presentation = "first name")
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @EntityField(presentation = "last name")
    @Column(name = "last_name", length = 100)
    private String lastName;

    @EntityField(presentation = "e-mail")
    @Column(name = "email", length = 100)
    private String email;

    @EntityField(presentation = "orders", skippedOnSearch = true, skippedOnInsert = true, skippedOnUpdate = true)
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Order> orders;

    public Customer() {
        this.orders = new ArrayList<>();
    }

    public Customer(String firstName, String lastName, String email) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return String.format("Customer (id: %d, fn: %s, ln: %s, email: %s, orders count: %d)",
                id,
                firstName,
                lastName,
                email,
                (orders != null ? orders.size(): 0));
    }
}