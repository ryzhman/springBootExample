package com.go2it.edu.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class checks each request for authentication JWT token.
 * If the token is absent or expired, client is redirected to /login page
 *
 * @author oleksandr.ryzhkov
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private String secretSalt;

    public JWTAuthorizationFilter(AuthenticationManager authManager, String secretSalt) {
        super(authManager);
        this.secretSalt = secretSalt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null) {
                // parse the token.
                String userDetails = JWT.require(Algorithm.HMAC512(secretSalt.getBytes()))
                        .build()
                        .verify(token.replace("Bearer ", ""))
                        .getSubject();

                if (userDetails != null) {
                    JsonNode jsonNode = new ObjectMapper().readTree(userDetails);
                    Iterator<JsonNode> authoritiesIterator = jsonNode.get("authorities").iterator();
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    while (authoritiesIterator.hasNext()) {
                        authorities.add(new SimpleGrantedAuthority(authoritiesIterator.next().get("authority").textValue()));
                    }
                    return new UsernamePasswordAuthenticationToken(jsonNode.get("userName"), null, authorities);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}