package spring.security.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import spring.security.domain.*;
import spring.security.repository.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InitDatabase {

    private final InitService initService;

    /**
     * WAS가 초기화 된 직후 바로 실행 될 메서드
     *
     * @see @PostConstruct
     */
    @PostConstruct
    public void init() {
        initService.createAdminAndRole();
    }

    @Service
    @RequiredArgsConstructor
    static class InitService {

        private final RoleRepository roleRepository;

        private final AdminRepository adminRepository;

        private final ResourceRepository resourceRepository;

        private final RoleResourceRepository roleResourceRepository;

        private final RoleHierarchyRepository roleHierarchyRepository;

        public void createAdminAndRole() {

            String password = PasswordEncoderFactories
                    .createDelegatingPasswordEncoder()
                    .encode("root");

            Map<String, Role> roles = createRoles();

            roleRepository.save(roles.get("sys"));
            roleRepository.save(roles.get("admin"));
            roleRepository.save(roles.get("user"));

            Admin aSys = createAdmin("sys", password, "ROLE_SYS_ADMIN", roles.get("sys"));
            Admin aAdmin = createAdmin("admin", password, "ROLE_ADMIN", roles.get("admin"));
            Admin aUser = createAdmin("user", password, "ROLE_USER", roles.get("user"));

            adminRepository.save(aSys);
            adminRepository.save(aAdmin);
            adminRepository.save(aUser);

            Resource rHome = createResource("/", "GET");
            Resource rUser = createResource("/user", "GET");
            Resource rAdmin = createResource("/admin", "GET");
            Resource rSys = createResource("/sys", "GET");

            resourceRepository.save(rHome);
            resourceRepository.save(rUser);
            resourceRepository.save(rAdmin);
            resourceRepository.save(rSys);

            roleResourceRepository.save(createRoleResource(roles.get("sys"), rSys));
            roleResourceRepository.save(createRoleResource(roles.get("sys"), rAdmin));
            roleResourceRepository.save(createRoleResource(roles.get("sys"), rUser));
            roleResourceRepository.save(createRoleResource(roles.get("sys"), rHome));


            roleResourceRepository.save(createRoleResource(roles.get("admin"), rAdmin));
            roleResourceRepository.save(createRoleResource(roles.get("admin"), rUser));
            roleResourceRepository.save(createRoleResource(roles.get("admin"), rHome));


            roleResourceRepository.save(createRoleResource(roles.get("user"), rUser));
            roleResourceRepository.save(createRoleResource(roles.get("user"), rHome));

            createHierarchy();

        }

        private void createHierarchy() {
            RoleHierarchy sys = RoleHierarchy.builder()
                                             .authority("ROLE_SYS_ADMIN")
                                             .parent(null)
                                             .build();
            roleHierarchyRepository.save(sys);

            RoleHierarchy admin = RoleHierarchy.builder()
                                               .authority("ROLE_ADMIN")
                                               .parent(roleHierarchyRepository.findById(sys.getId()).get())
                                               .build();
            roleHierarchyRepository.save(admin);

            RoleHierarchy user = RoleHierarchy.builder()
                                              .authority("ROLE_USER")
                                              .parent(roleHierarchyRepository.findById(admin.getId()).get())
                                              .build();
            roleHierarchyRepository.save(user);

        }

        private Map<String, Role> createRoles() {

            Map<String, Role> roles = new HashMap<>();

            roles.put("sys", Role.builder()
                                 .authorityId("ROLE_SYS_ADMIN")
                                 .description("시스템관리자")
                                 .deleted(false)
                                 .build());

            roles.put("admin", Role.builder()
                                   .authorityId("ROLE_ADMIN")
                                   .description("관리자")
                                   .deleted(false)
                                   .build());

            roles.put("user", Role.builder()
                                  .authorityId("ROLE_USER")
                                  .description("유저")
                                  .deleted(false)
                                  .build());

            return roles;

        }

        private Admin createAdmin(String username, String password, String authorityId, Role role) {
            return Admin.builder()
                        .username(username)
                        .password(password)
                        .authorityId(role)
                        .build();
        }

        private RoleResource createRoleResource(Role role, Resource resource) {
            return RoleResource.builder()
                               .role(role)
                               .resource(resource)
                               .deleted(false)
                               .build();
        }

        private Resource createResource(String url, String method) {
            return Resource.builder()
                           .url(url)
                           .method(method)
                           .deleted(false)
                           .build();
        }

    }

}