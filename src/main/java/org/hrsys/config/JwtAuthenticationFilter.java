package org.hrsys.config;

import org.hrsys.exception.InvalidTokenException;
import org.hrsys.model.ApiError;
import org.hrsys.service.UserDetailsService;
import org.hrsys.util.Constants;
import org.hrsys.util.security.JwtUtil;
import org.json.JSONObject;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/login")) {
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
            String role = JwtUtil.getRole(token);

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            UserDetails userDetails = new User(email, "", authorities);


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