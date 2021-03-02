package spring.security.security.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.security.domain.Resource;
import spring.security.repository.ResourceRepository;
import spring.security.repository.RoleResourceRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class SecurityResourceService {

    private static final Logger log = LoggerFactory.getLogger(SecurityResourceService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private RoleResourceRepository roleResourceRepository;

    @Transactional
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

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

        if(log.isInfoEnabled()) {
            log.info("SecurityResourceService.getResourceList result: \n");
            result.entrySet().forEach(re -> {
                log.info(re.getKey() + " : " + re.getValue());
            });
        }

        return result;
    }

}
