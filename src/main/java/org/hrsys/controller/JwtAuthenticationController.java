package org.hrsys.controller;

import org.hrsys.model.JwtRequest;
import org.hrsys.model.JwtResponse;
import org.hrsys.service.UserDetailsService;
import org.hrsys.util.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class JwtAuthenticationController {

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

         String token = userDetailsService.generateToken(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}