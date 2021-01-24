package ru.oshokin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oshokin.persist.repos.CustomerRepository;

import java.util.stream.Collectors;

@Service
public class CustomerAuthService implements UserDetailsService {

    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerAuthService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customerRepository.findCustomersByEmail(email)
                .map(customer -> new User(
                        customer.getEmail(),
                        customer.getPassword(),
                        customer.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toList())
                )).orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }

}