package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.security.domain.Resource;
import spring.security.domain.Role;
import spring.security.domain.RoleResource;

import java.util.List;
import java.util.Map;

public interface RoleResourceRepository extends JpaRepository<RoleResource, Long> {

    List<RoleResource> findByResource(Resource resource);

}
