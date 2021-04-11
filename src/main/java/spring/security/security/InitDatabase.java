package spring.security.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDatabase implements ApplicationListener<ContextRefreshedEvent> {
    
    private boolean alreadySetup = false;
    private final InitService initService;
    
    @Override
    public void onApplicationEvent (ContextRefreshedEvent event) {
        
        if(log.isDebugEnabled()) {
            log.debug("onApplicationEvent start");
        }
        
        if(alreadySetup) {
            return;
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Application setup...");
        }
        initService.setUp();
        
        alreadySetup = true;
        
    }
    
    @Service
    @RequiredArgsConstructor
    static class InitService {
        
        private final RoleRepository roleRepository;
        private final AdminRepository adminRepository;
        private final ResourceRepository resourceRepository;
        private final RoleResourceRepository roleResourceRepository;
        private final RoleHierarchysRepository roleHierarchysRepository;
        private final UrlFilterInvocationSecurityMetadataSource metadataSource;
        
        public void setUp () {
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
        
        private void createHierarchy () {
            RoleHierarchys sys = RoleHierarchys.builder()
                                               .authority("ROLE_SYS_ADMIN")
                                               .orders(1)
                                               .build();
            roleHierarchysRepository.save(sys);
            
            RoleHierarchys admin = RoleHierarchys.builder()
                                                 .authority("ROLE_ADMIN")
                                                 .orders(2)
                                                 .build();
            roleHierarchysRepository.save(admin);
            
            RoleHierarchys user = RoleHierarchys.builder()
                                                .authority("ROLE_USER")
                                                .orders(3)
                                                .build();
            roleHierarchysRepository.save(user);
            
            RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
            List<RoleHierarchys> sysRoles = roleHierarchysRepository.findRoleHierarchysByOrders(1);
            List<RoleHierarchys> adminRoles = roleHierarchysRepository.findRoleHierarchysByOrders(2);
            List<RoleHierarchys> userRoles = roleHierarchysRepository.findRoleHierarchysByOrders(3);
            
            StringBuilder sb = new StringBuilder();
            
            for(int i = 0; i < sysRoles.size(); i++) {
                sb.append(sysRoles.get(i).getAuthority());
                if(i != sysRoles.size() - 1) {
                    sb.append(" > ");
                }
                else {
                    sb.append(" AND ");
                }
            }
            for(int i = 0; i < adminRoles.size(); i++) {
                sb.append(adminRoles.get(i).getAuthority());
                if(i != adminRoles.size() - 1) {
                    sb.append(" > ");
                }
                else {
                    sb.append(" AND ");
                }
            }
            for(int i = 0; i < userRoles.size(); i++) {
                sb.append(userRoles.get(i).getAuthority());
                if(i != userRoles.size() - 1) {
                    sb.append(" > ");
                }
            }
            
            log.info("RoleHierarchy configure: " + sb.toString());
            
            roleHierarchy.setHierarchy(sb.toString());
            
        }
        
        private Map<String, Role> createRoles () {
            
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