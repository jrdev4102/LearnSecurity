package spring.security.security.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import spring.security.repository.RoleHierarchyRepository;
import spring.security.security.service.CustomUserDetailsService;
import spring.security.security.service.SecurityResourceService;

import java.util.ArrayList;
import java.util.List;

/**
 * WAS 가 초기화 되며 WebSecurityConfigurer 를 구현한 클래스나 WebSecurityConfigurerAdapter 를 상속한 클래스의 설정 정보를 토대로 Spring Security FilterChainProxy 가 초기화 된다.
 * <br /><br />
 * DelegatingFilterProxy 는 springSecurityFilterChain 이라는 이름(name)을 가진 Spring Bean 을 찾아 Spring Security 의 보안 과정을 위임한다.
 * springSecurityFilterChain 은 FilterChainProxy 의 이름이기도 하다.
 * <br /><br />
 * FilterSecurityInterceptor 는 springSecurityFilterChain 의 마지막 필터로 최종적인 인가판단을 한다. 이 판단의 관리를
 * AccessDecisionManager 에 위임하며 AccessDecisionManager 는 인증정보, 요청정보, 인가정보를 토대로 AccessDecisionVoter 에게 판단(decide)을 위임한다.
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //---------------------------------- Fields ----------------------------------//

    private final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final RoleHierarchyRepository roleHierarchyRepository;
    private final SecurityResourceService securityResourceService;
    private String[] permitAllResources = { "/", "/join", "/login", "/logout" };

    //---------------------------------- Settings ----------------------------------//

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
           .requestMatchers(
                   PathRequest
                           .toStaticResources()
                           .atCommonLocations()
                           );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable()
                .csrf().disable();

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());

        http
                .formLogin()
                .failureUrl("/login")
                .defaultSuccessUrl("/")
                .permitAll();

        http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

        http
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
    }

    //---------------------------------- Methods ----------------------------------//

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoters());
        return affirmativeBased;
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(roleVoter());
        return accessDecisionVoters;
    }

    //---------------------------------- Beans ----------------------------------//

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        return accessDeniedHandler;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        List<spring.security.domain.RoleHierarchy> roles = roleHierarchyRepository.findAll();
    
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            result.append(roles.get(i).getAuthority());
            if (i != roles.size() - 1) {
                result.append(" > ");
            }
        }
    
        if (log.isInfoEnabled()) {
            log.info("RoleHierarchy configure: " + result.toString());
        }
        roleHierarchy.setHierarchy(result.toString());
        return roleHierarchy;
    }

    @Bean
    public SecurityExpressionHandler<FilterInvocation> expressionHandler() {
        DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        webSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return webSecurityExpressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        return roleHierarchyVoter;
    }

    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

}
