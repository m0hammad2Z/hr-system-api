package org.hrsys.service;

import org.hrsys.exception.InvalidRequestException;
import org.hrsys.exception.ResourceNotFoundException;
import org.hrsys.model.Employee;
import org.hrsys.model.Manager;
import org.hrsys.repository.EmployeeRepository;
import org.hrsys.repository.ManagerRepository;
import org.hrsys.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    public ManagerService(ManagerRepository managerRepository, EmployeeRepository employeeRepository, RoleRepository roleRepository) {
        this.managerRepository = managerRepository;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Manager getManagerById(Long id) {
        return managerRepository.findByEmployeeId(id).orElseThrow(() -> new ResourceNotFoundException("Manager", id));
    }


    @Transactional
    public Manager upgradeToManager(Long employeeId) {
        // Check if the employee exists and is not already a manager
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        if (employee.getManager() == null) {
            throw new InvalidRequestException("The specified employee is already a manager.");
        }

        employee.setManager(null);
        //change the role of the employee to manager
        employee.setRole(roleRepository.findByName("ROLE_MANAGER").orElseThrow(() -> new ResourceNotFoundException("Role", "ROLE_MANAGER")));

        employeeRepository.save(employee);

        // Upgrade the employee to a manager
        return managerRepository.save(new Manager(employeeId));

    }
}