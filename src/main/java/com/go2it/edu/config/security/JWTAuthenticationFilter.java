package com.go2it.edu.config.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.go2it.edu.entity.User;
import com.go2it.edu.entity.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * This class filters the login requests.
 * It checks of the credentials passed in form-data are correct and then creates a JWT token with some user details.
 * The token is passed back in Authorization header
 *
 * @author oleksandr.ryzhkov
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private String secretSalt;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String secretSalt) {
        this.authenticationManager = authenticationManager;
        this.secretSalt = secretSalt;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        User userFromCrederntials = new User();
        userFromCrederntials.setUserName(req.getParameter("username"));
        userFromCrederntials.setPassword(req.getParameter("password"));
        UserPrincipal creds = new UserPrincipal(userFromCrederntials);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(),
                        creds.getPassword(),
                        Collections.emptyList())
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws JsonProcessingException {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = JWT.create()
                .withSubject(new ObjectMapper().writeValueAsString(principal))
                .withExpiresAt(Date.from(LocalDateTime.now().plus(30, ChronoUnit.MINUTES)
                        .atZone(ZoneId.systemDefault()).toInstant())
                ).sign(HMAC512(secretSalt.getBytes()));
        res.addHeader("Authorization", "Bearer " + token);
    }
}
