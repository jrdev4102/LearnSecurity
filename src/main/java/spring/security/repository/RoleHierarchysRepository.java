package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.domain.RoleHierarchys;

import java.util.List;

public interface RoleHierarchysRepository extends JpaRepository<RoleHierarchys, Long> {
    
    List<RoleHierarchys> findRoleHierarchysByOrders (int orders);
    
}
