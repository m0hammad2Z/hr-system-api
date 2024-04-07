package org.hrsys.service;

import org.hrsys.model.Employee;
import org.hrsys.repository.EmployeeRepository;
import org.hrsys.util.security.JwtUtil;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDetailsService(EmployeeRepository employeeRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.employeeRepository = employeeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = findEmployeeByEmail(username); // find employee by email
        return User.withUsername(employee.getEmail())
                .password(employee.getPassword())
                .authorities(employee.getRole().getName())
                .build();
    }

    // Generate token for the user with the given email and password
    public String generateToken(String email, String password) {
        Employee employee = findEmployeeByEmail(email);
        if (!bCryptPasswordEncoder.matches(password, employee.getPassword())) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return JwtUtil.generateToken(email, employee.getRole().getName());
    }

    // Find employee by email
    private Employee findEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null || employee.getRole() == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return employee;
    }
}