package com.go2it.edu.config.security;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        http
                //By default is enabled for POST requests
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/api/welcome")
                    .permitAll()
                //				even anonymous user is authenthicated
                //				.authenticated()
                //				 .hasAnyRole()
                    .antMatchers(HttpMethod.POST, "/api/users")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/users")
                    .hasAnyRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/customizedWelcome")
                    .hasRole("USER")
                    .antMatchers(HttpMethod.GET, "/api/resource/**")
                    .hasRole("USER")
                .and()
                    .formLogin()
                    .permitAll()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), secretSalt))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), secretSalt))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);;
    }
}
