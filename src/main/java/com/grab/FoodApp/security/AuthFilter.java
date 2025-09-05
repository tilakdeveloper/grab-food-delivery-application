package com.grab.FoodApp.security;

import com.grab.FoodApp.exceptions.CustomAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if(token != null) {
            String email;
            try {
                email = jwtUtils.getUsernameFromToken(token);

            } catch (Exception ex) {
                AuthenticationException authenticationException = new BadCredentialsException(ex.getMessage());
                log.error("Authentication error: {}", ex.getMessage());
                customAuthenticationEntryPoint.commence(request, response, authenticationException);
                return;
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            if(StringUtils.hasText(email) && jwtUtils.isTokenValid(token, userDetails)) {
                log.info("Token is valid for user: {}", email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                AuthenticationException authenticationException = new BadCredentialsException("Invalid token");
                log.error("Authentication error: Invalid token");
                customAuthenticationEntryPoint.commence(request, response, authenticationException);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String tokenWithBearer = request.getHeader("Authorization");
        if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
            return tokenWithBearer.substring(7);
        }
        return null;
    }


}
