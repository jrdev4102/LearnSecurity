package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.domain.Role;
import spring.security.domain.RoleResource;

public interface RoleResourceRepository extends JpaRepository<RoleResource, Long> {

}
