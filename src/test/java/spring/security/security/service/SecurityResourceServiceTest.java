package spring.security.security.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.security.domain.RoleHierarchys;
import spring.security.repository.RoleHierarchysRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityResourceServiceTest {
    
    @Autowired
    private RoleHierarchysRepository roleHierarchysRepository;
    
    @Test
    @DisplayName("시큐리티_권한계층")
    public void assembleAuthorityHierarchy () throws Exception {
        List<RoleHierarchys> systems = roleHierarchysRepository.findRoleHierarchysByOrders(1);
        List<RoleHierarchys> admins = roleHierarchysRepository.findRoleHierarchysByOrders(2);
        List<RoleHierarchys> users = roleHierarchysRepository.findRoleHierarchysByOrders(3);
        
        assertThat(systems).flatExtracting(RoleHierarchys :: getOrders).contains(1);
        assertThat(admins).flatExtracting(RoleHierarchys :: getOrders).contains(2);
        assertThat(users).flatExtracting(RoleHierarchys :: getOrders).contains(3);
    }
    
}