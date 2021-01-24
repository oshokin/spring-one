package ru.oshokin.persist.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oshokin.persist.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}