package spring.security.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.security.domain.RoleHierarchy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RoleHierarchyRepositoryTest {

    @Autowired
    private RoleHierarchyRepository roleHierarchyRepository;

    @Test
    @DisplayName("권한상속_저장")
    public void save() throws Exception {
        // Given
        RoleHierarchy sys = RoleHierarchy.builder()
                                         .authority("ROLE_SYS_ADMIN")
                                         .parent(null)
                                         .build();

        RoleHierarchy admin = RoleHierarchy.builder()
                                           .authority("ROLE_ADMIN")
                                           .parent(sys)
                                           .build();

        RoleHierarchy user = RoleHierarchy.builder()
                                          .authority("ROLE_USER")
                                          .parent(admin)
                                          .build();

        // When
        roleHierarchyRepository.save(user);
        roleHierarchyRepository.save(admin);
        roleHierarchyRepository.save(sys);

        // Then
        assertThat(sys.getAuthority()).isEqualTo("ROLE_SYS_ADMIN");
        assertThat(sys.getParent()).isNull();

        assertThat(admin.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(admin.getParent()).isSameAs(sys);

        assertThat(user.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(user.getParent()).isSameAs(admin);
    }

    @Test
    @DisplayName("권한상속_조회")
    public void findRoleHierarchy() throws Exception {
        // Given
        StringBuilder result = new StringBuilder("");
        List<RoleHierarchy> roles = roleHierarchyRepository.findAll();

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