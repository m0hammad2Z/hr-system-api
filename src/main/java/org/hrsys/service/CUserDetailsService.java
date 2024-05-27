package org.hrsys.service;

import jakarta.persistence.EntityManager;
import org.hrsys.model.Employee;
import org.hrsys.repository.EmployeeRepository;
import org.hrsys.util.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Get employee by email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = findEmployeeByEmail(username);
        return User.withUsername(employee.getEmail())
                .password(employee.getPassword())
                .authorities(employee.getRole().getName())
                .build();
    }

    // Generate token for the user with the given email and password
    public String generateToken(String email) {
        Employee employee = findEmployeeByEmail(email);
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
