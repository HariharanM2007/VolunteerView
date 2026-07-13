package com.example.demo.security;

import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final SystemUserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        final String jwt;

        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;

        }

        jwt = authHeader.substring(7);

        try {

            username = jwtService.extractUsername(jwt);

        } catch (Exception ex) {

            filterChain.doFilter(request, response);

            return;

        }

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            Optional<SystemUser> optionalUser =
                    userRepository.findByUsername(username);

            if (optionalUser.isPresent()) {

                SystemUser user = optionalUser.get();

                if (!user.getIsActive()) {

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    response.setContentType("application/json");

                    HashMap<String, Object> body = new HashMap<>();

                    body.put("timestamp", LocalDateTime.now().toString());
                    body.put("status", 403);
                    body.put("error", "Forbidden");
                    body.put("message", "Account is deactivated.");

                    new ObjectMapper().writeValue(
                            response.getOutputStream(),
                            body
                    );

                    return;

                }

                if (jwtService.isTokenValid(jwt, user.getUsername())) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                }

            }

        }

        filterChain.doFilter(request, response);

    }

}