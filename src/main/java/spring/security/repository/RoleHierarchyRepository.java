package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.domain.RoleHierarchy;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {

}
