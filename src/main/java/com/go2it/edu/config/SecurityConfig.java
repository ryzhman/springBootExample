package com.go2it.edu.config;

import com.go2it.edu.service.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * @author Alex Ryzhkov
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private int strength;
    private String secretSalt;
    @Autowired
    private SecurityUserService securityUserService;

    public SecurityConfig(@Value("${security.strength:10}") int strength,
                          @Value("${security.secretSalt:$2a$10$EzbrJCN8wj8M8B5aQiRmiuWqVvnxna73Ccvm38aoneiJb88kkwlH2}") String secretSalt) {
        super();
        this.strength = strength;
        this.secretSalt = secretSalt;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(securityUserService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //manually added users with passwords will fail the validation since they can be encrypted only by this method
        return new BCryptPasswordEncoder(strength, new SecureRandom(secretSalt.getBytes()));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                //By default is enabled for any modifying requests (POST, PUT etc)
//                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/api/welcome")
                    .permitAll()
                //				even anonymous user is authenthicated
                //				.authenticated()
                //				 .hasAnyRole()
                    .antMatchers(HttpMethod.POST, "/api/users")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/users")
                    .permitAll()
                    .antMatchers(HttpMethod.POST, "/api/customizedWelcome")
                    .hasRole("USER")
                    .antMatchers("/api/users/**")
                    .hasAnyRole("ADMIN")
                .and()
                    .formLogin()
                    .permitAll()
                .and()
                    .csrf().csrfTokenRepository(csrfTokenRepository())
                //Add the CSRF token explicitly to the request cookies to be able to re-use it
                //on per-session case
                .and()
                    .addFilterAfter(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                                                    HttpServletResponse response, FilterChain filterChain)
                            throws IOException, ServletException {
                        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                                .getName());
                        if (csrf != null) {
                            Cookie cookie = WebUtils.getCookie(request, "X-CSRF-TOKEN");
                            String token = csrf.getToken();
                            if (cookie == null || token != null
                                    && !token.equals(cookie.getValue())) {
                                cookie = new Cookie("X-CSRF-TOKEN", token);
                                cookie.setPath("/");
                                response.addCookie(cookie);
                            }
                        }
                        filterChain.doFilter(request, response);
                    }
                    }, CsrfFilter.class);
    }

    /**
     * Add CSRF token to each response as a header
     * @return
     */
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }
}
