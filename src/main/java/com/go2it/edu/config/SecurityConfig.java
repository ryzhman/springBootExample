package com.go2it.edu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Alex Ryzhkov
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	public SecurityConfig() {
		super();
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("admin")
				.password(encoder().encode("adminPass"))
				.roles("ADMIN")
				.and()
				.withUser("user")
				.password(encoder().encode("userPass"))
				.roles("USER");
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic()
				.and()
				.authorizeRequests()
				.antMatchers("/api/welcome")
				.permitAll()
				//				even anonymous user is authenthicated
				//				.authenticated()
				//				 .hasAnyRole()
				.antMatchers(HttpMethod.POST, "/api/customizedWelcome")
				.permitAll()
				.antMatchers("/api/user/**")
				.hasAnyRole("ADMIN", "USER")
				.antMatchers("/api/resource/**")
				.hasRole("ADMIN")
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.permitAll();
	}
}
