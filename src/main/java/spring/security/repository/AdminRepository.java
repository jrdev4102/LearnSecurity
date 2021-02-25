package spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.domain.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
