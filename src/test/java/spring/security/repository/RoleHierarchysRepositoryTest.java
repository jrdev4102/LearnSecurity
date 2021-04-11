package spring.security.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.security.domain.RoleHierarchys;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RoleHierarchysRepositoryTest {
    
    @Autowired
    private RoleHierarchysRepository roleHierarchysRepository;
    
    @Test
    @DisplayName("권한상속_저장")
    public void save () throws Exception {
        // Given
        RoleHierarchys sys = RoleHierarchys.builder()
                                           .authority("ROLE_SYS_ADMIN")
                                           .orders(1)
                                           .build();
        
        RoleHierarchys admin = RoleHierarchys.builder()
                                             .authority("ROLE_ADMIN")
                                             .orders(2)
                                             .build();
        
        RoleHierarchys user = RoleHierarchys.builder()
                                            .authority("ROLE_USER")
                                            .orders(3)
                                            .build();
        
        // When
        roleHierarchysRepository.save(user);
        roleHierarchysRepository.save(admin);
        roleHierarchysRepository.save(sys);
        
        // Then
        assertThat(sys.getAuthority()).isEqualTo("ROLE_SYS_ADMIN");
        assertThat(sys.getOrders()).isEqualTo(1);
        
        assertThat(admin.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(admin.getOrders()).isEqualTo(2);
        
        assertThat(user.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(user.getOrders()).isEqualTo(3);
    }

    @Test
    @DisplayName("권한상속_조회")
    public void findRoleHierarchy() throws Exception {
        // Given
        StringBuilder result = new StringBuilder();
        List<RoleHierarchys> roles = roleHierarchysRepository.findAll();

        // When
        for(int i = 0; i < roles.size(); i++) {
            result.append(roles.get(i).getAuthority());
            if(i != roles.size() - 1) {
                result.append(">");
            }
        }

        // Then
        System.out.println("result = " + result);
    }

}