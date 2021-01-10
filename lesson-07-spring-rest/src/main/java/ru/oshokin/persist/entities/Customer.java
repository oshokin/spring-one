package ru.oshokin.persist.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "E-mail shouldn''t be blank")
    @Column(name = "email", length = 100)
    private String email;

    @NotBlank(message = "Password shouldn''t be blank")
    @Column(name = "password", length = 100)
    private String password;

    @NotBlank(message = "First name shouldn''t be blank")
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @NotBlank(message = "Last name shouldn''t be blank")
    @Column(name = "last_name", length = 100)
    private String lastName;

    public Customer() {
    }

    public Customer(Long id, String firstName, String lastName, String email) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

}