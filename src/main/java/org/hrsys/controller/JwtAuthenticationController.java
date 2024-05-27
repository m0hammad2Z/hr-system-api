package org.hrsys.controller;

import org.hrsys.model.Employee;
import org.hrsys.model.JwtRequest;
import org.hrsys.model.JwtResponse;
import org.hrsys.service.CUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class JwtAuthenticationController {

    @Autowired
    private CUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
            if(authentication.isAuthenticated()) {
                String token = userDetailsService.generateToken(authenticationRequest.getEmail());
                return ResponseEntity.ok(new JwtResponse(token));
            }
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid email or password", e);
        }
        throw new Exception("Invalid email or password");
    }
}
