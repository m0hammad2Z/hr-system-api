package org.hrsys.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hrsys.exception.InvalidTokenException;
import org.hrsys.model.ApiError;
import org.hrsys.service.CUserDetailsService;
import org.hrsys.util.Constants;
import org.hrsys.util.security.JwtUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CUserDetailsService userDetailsService;

    private static String uriKeyWords[] = {"/login", "/swagger", "/v3/api-docs", "/webjars"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (Arrays.stream(uriKeyWords).anyMatch(requestURI::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String requestTokenHeader = request.getHeader(Constants.HEADER_STRING);
            if (requestTokenHeader == null) {
                throw new InvalidTokenException("No token provided");
            }
            String token = JwtUtil.resolveToken(requestTokenHeader);
            JwtUtil.validateToken(token);

            String email = JwtUtil.getEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, e.getMessage(), new ArrayList<>());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new JSONObject(apiError).toString());
        }
    }
}

