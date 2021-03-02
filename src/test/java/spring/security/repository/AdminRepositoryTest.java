package spring.security.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.security.domain.Admin;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("관리자_조회")
    public void findAdministrator() throws Exception {
        // Given
        String username = "sys";

        // When
        Admin admin = adminRepository.findByUsername(username);

        // Then
        assertThat(admin.getUsername()).isEqualTo("sys");
    }

}