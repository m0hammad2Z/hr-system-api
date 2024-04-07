package org.hrsys.service;

import org.hrsys.exception.InvalidRequestException;
import org.hrsys.exception.ResourceNotFoundException;
import org.hrsys.model.Employee;
import org.hrsys.model.Manager;
import org.hrsys.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ManagerRepository managerRepository;
    private final RoleRepository roleRepository;
    private final LeaveRepository leaveRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, ManagerRepository managerRepository, RoleRepository roleRepository, LeaveRepository leaveRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.managerRepository = managerRepository;
        this.roleRepository = roleRepository;
        this.leaveRepository = leaveRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        if(employee == null){
            throw new InvalidRequestException("Employee must not be null");
        }

        // Check if the email is already in use
        if (employeeRepository.findByEmail(employee.getEmail()) != null) {
            throw new InvalidRequestException("The specified email is already in use.");
        }

        // Check if the department and role exist
        employee.setDepartment(departmentRepository.findById(employee.getDepartment().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", employee.getDepartment().getId())));
        employee.setRole(roleRepository.findById(employee.getRole().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", employee.getRole().getId())));

        // Encrypt the password before saving
        employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
        employee.setCreationDate(LocalDate.now());


        Employee savedEmployee;

        if(isManager(employee)){
            employee.setManager(null);
            savedEmployee = employeeRepository.save(employee);
            Manager manager = new Manager(savedEmployee.getId());
            managerRepository.save(manager);
        }else if (!isManager(employee) && employee.getManager() == null){
            throw new InvalidRequestException("Employee must have a manager");
        }else{
            Manager manager = managerRepository.findByEmployeeId(employee.getManager().getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", employee.getManager().getEmployeeId()));
            employee.setManager(manager);
            savedEmployee = employeeRepository.save(employee);
        }

        return savedEmployee;
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        validateEmployeeId(id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    @Transactional
    public Employee updateEmployee(Employee employee) {
        // Validate the employee
        validateEmployee(employee);
        // Validate the employee ID
        validateEmployeeId(employee.getId());

        // Optimistic locking
        if(employee.getVersion() == null || employeeRepository.findById(employee.getId()).get().getVersion() != employee.getVersion()){
            throw new InvalidRequestException("The provided version does not match the current version");
        }

        // Retrieve the existing employee from the database
        Employee existingEmployee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employee.getId()));


        // Update the existing employee's attributes with the new values
        updateExistingEmployee(existingEmployee, employee);

        return employeeRepository.save(existingEmployee);
    }


    @Transactional
    public void deleteEmployee(Long oldEmployeeId, Long newEmployeeId) {
        // Check if the employee exists
        Employee employee = employeeRepository.findById(oldEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", oldEmployeeId));

        // If the employee is a manager, reassign his/her subordinates to the new manager
        if (isManager(employee)) {
            Manager oldManager = managerRepository.findByEmployeeId(oldEmployeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", oldEmployeeId));

            Manager newManager = managerRepository.findByEmployeeId(newEmployeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", newEmployeeId));

            // Reassign the subordinates of the old manager to the new manager
            for (Employee subordinate : oldManager.getEmployees()) {
                subordinate.setManager(newManager);
            }

            employeeRepository.saveAll(oldManager.getEmployees());
            // Delete the old manager
            managerRepository.delete(oldManager);
        }

        // Delete the employee
        employeeRepository.delete(employee);
    }

    // Get employees created between two dates
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByCreationDateBetween(LocalDate from, LocalDate to) {
        // Check if the from and to dates are not null
        if (from == null || to == null) {
            throw new InvalidRequestException("From and to dates must not be null");
        }

        // Check if the from date is before the to date
        if (from.isAfter(to)) {
            throw new InvalidRequestException("From date must be before to date");
        }

        return employeeRepository.findByCreationDateBetween(from, to);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        if (departmentId == null) {
            throw new InvalidRequestException("Department ID must not be null");
        }
        return employeeRepository.findByDepartmentId(departmentId);
    }


// ----------------- Helper Methods -------------------

    // Check if the employee is a manager
    private boolean isManager(Employee employee){
        return Objects.nonNull(employee.getRole()) && employee.getRole().getName().equals("ROLE_MANAGER");
    }

    // Validate the employee if got something to be updated
    private void validateEmployee(Employee employee) {
        if (Objects.isNull(employee.getName())
                && Objects.isNull(employee.getEmail())
                && Objects.isNull(employee.getCreationDate())
                && Objects.isNull(employee.getDepartment())
                && Objects.isNull(employee.getPassword())) {
            throw new InvalidRequestException("Employee must have at least one attribute to be updated");
        }
    }

    // Method to validate an employee ID
    private void validateEmployeeId(Long id) {
        if (id == null || !employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
    }

    // Method to update an existing employee
    private void updateExistingEmployee(Employee existing, Employee employee) {
        // Update the employee attributes
        existing.setName(Optional.ofNullable(employee.getName()).orElse(existing.getName()));

        // Check if the email is not already in use
        if (!employee.getEmail().equals(existing.getEmail()) && employeeRepository.findByEmail(employee.getEmail()) != null) {
            throw new InvalidRequestException("The specified email is already in use.");
        }
        existing.setEmail(Optional.ofNullable(employee.getEmail()).orElse(existing.getEmail()));

        if (employee.getCreationDate() != null) {
            existing.setCreationDate(employee.getCreationDate());
        }
        if (employee.getDepartment() != null) {
            existing.setDepartment(departmentRepository.findById(employee.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", employee.getDepartment().getId())));
        }

        if (employee.getManager() != null) {
            Manager manager = managerRepository.findById(employee.getManager().getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", employee.getManager().getEmployeeId()));
            existing.setManager(manager);
        }

        if (employee.getPassword() != null) {
            existing.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
        }
    }
}