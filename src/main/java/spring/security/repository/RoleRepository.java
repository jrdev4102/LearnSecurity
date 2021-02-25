package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.domain.Admin;
import spring.security.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
