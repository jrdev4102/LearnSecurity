package spring.security.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import spring.security.domain.*;
import spring.security.repository.*;
import spring.security.security.config.UrlFilterInvocationSecurityMetadataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application 시작 시 초기 데이터 설정을 진행하고 로그를 기록하는 클래스
 */
@Component
@RequiredArgsConstructor
public class InitDatabase implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(InitDatabase.class);

    private boolean alreadySetup = false;

    private final InitService initService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(log.isInfoEnabled()) {
            log.info("onApplicationEvent start");
        }

        if(alreadySetup) {
            return;
        }

        if(log.isInfoEnabled()) {
            log.info("Application setup...");
            initService.setUp();
        }

        alreadySetup = true;

    }

    /**
     * Application 시작 시 초기 데이터 설정을 정의한 클래스
     */
    @Service
    @RequiredArgsConstructor
    static class InitService {

        private final RoleRepository roleRepository;

        private final AdminRepository adminRepository;

        private final ResourceRepository resourceRepository;

        private final RoleResourceRepository roleResourceRepository;

        private final RoleHierarchyRepository roleHierarchyRepository;

        private final UrlFilterInvocationSecurityMetadataSource metadataSource;

        public void setUp() {

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
            metadataSource.reload();

        }

        private void createHierarchy() {
            RoleHierarchy sys = RoleHierarchy.builder()
                                             .authority("ROLE_SYS_ADMIN")
                                             .parent(null)
                                             .build();
            roleHierarchyRepository.save(sys);

            RoleHierarchy admin = RoleHierarchy.builder()
                                               .authority("ROLE_ADMIN")
                                               .parent(sys)
                                               .build();
            roleHierarchyRepository.save(admin);

            RoleHierarchy user = RoleHierarchy.builder()
                                              .authority("ROLE_USER")
                                              .parent(admin)
                                              .build();
            roleHierarchyRepository.save(user);

            RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
            List<RoleHierarchy> roles = roleHierarchyRepository.findAll();

            StringBuilder result = new StringBuilder("");
            for(int i = 0; i < roles.size(); i++) {
                result.append(roles.get(i).getAuthority());
                if(i != roles.size() - 1) {
                    result.append(" > ");
                }
            }

            if(log.isInfoEnabled()) {
                log.info("RoleHierarchy configure: " + result.toString());
            }
            roleHierarchy.setHierarchy(result.toString());

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