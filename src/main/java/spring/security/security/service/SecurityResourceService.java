package spring.security.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.security.domain.Resource;
import spring.security.domain.RoleHierarchys;
import spring.security.repository.ResourceRepository;
import spring.security.repository.RoleHierarchysRepository;
import spring.security.repository.RoleResourceRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityResourceService {
    
    private final ResourceRepository resourceRepository;
    private final RoleResourceRepository roleResourceRepository;
    private final RoleHierarchysRepository roleHierarchysRepository;
    
    @Transactional
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList () {
        
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        
        List<Resource> resources = resourceRepository.findAll();
        
        resources.forEach(resource -> {
            
            List<ConfigAttribute> configAttributes = new ArrayList<>();
            
            roleResourceRepository.findByResource(resource)
                                  .forEach(roleResource -> {
                                      configAttributes.add(new SecurityConfig(roleResource.getRole().getAuthorityId()));
                                      result.put(new AntPathRequestMatcher(resource.getUrl()), configAttributes);
                                  });
        });
        
        log.info("SecurityResourceService.getResourceList result: \n");
        result.entrySet().forEach(re -> {
            log.info(re.getKey() + " : " + re.getValue());
        });
        
        return result;
    }
    
    @Transactional
    public RoleHierarchyImpl assembleAuthorityHierarchy () {
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
        return roleHierarchy;
    }
    
}
