package com.devspark.childcare.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseJwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);

            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String email = decodedToken.getEmail();

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String role = (String) decodedToken.getClaims().get("role");
                    if (role == null) role = "USER";
                    setAuthentication(request, email, role);
                }
            } catch (Exception e) {
                log.error("Firebase token verification failed: ", e);
            }
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, String email, String role) {
        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        UserDetails userDetails = new User(email, "", authorities);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}