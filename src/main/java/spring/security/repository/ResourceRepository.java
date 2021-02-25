package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.domain.Resource;
import spring.security.domain.RoleResource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

}
