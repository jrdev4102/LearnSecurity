package spring.security.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.transaction.annotation.Transactional;
import spring.security.domain.Resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RoleResourceRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private RoleResourceRepository roleResourceRepository;

    private static final Logger log = LoggerFactory.getLogger(RoleResourceRepositoryTest.class);

    /**
     * Ant [pattern='/']=[ROLE_SYS_ADMIN, ROLE_ADMIN, ROLE_USER], <br />
     * Ant [pattern='/user']=[ROLE_SYS_ADMIN, ROLE_ADMIN, ROLE_USER], <br />
     * Ant [pattern='/admin']=[ROLE_SYS_ADMIN, ROLE_ADMIN], <br />
     * Ant [pattern='/sys']=[ROLE_SYS_ADMIN] <br />
     *
     * @throws Exception
     */
    @Test
    @DisplayName("LinkedHashMap<RequestMatcher, List<ConfigAttribute>> 생성")
    public void makeUpRequestMatcher() throws Exception {
        // given
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();

        List<Resource> resources = resourceRepository.findAll();

        // when
        resources.forEach(resource -> {

            List<ConfigAttribute> configAttributes = new ArrayList<>();

            roleResourceRepository.findByResource(resource)
                                  .forEach(roleResource -> {

                                      configAttributes.add(new SecurityConfig(roleResource.getRole().getAuthorityId()));

                                      result.put(new AntPathRequestMatcher(resource.getUrl()), configAttributes);

                                  });
        });

        // then
        assertThat(result.entrySet().size()).isEqualTo(resources.size());
        result.entrySet().forEach(re -> {
            log.info(() -> re.getKey() + " : " + re.getValue());
        });

    }

}

