package org.hrsys.service;

import org.hrsys.exception.InvalidRequestException;
import org.hrsys.exception.ResourceNotFoundException;
import org.hrsys.model.Department;
import org.hrsys.model.Directorate;
import org.hrsys.model.Employee;
import org.hrsys.repository.DepartmentRepository;
import org.hrsys.repository.DirectorateRepository;
import org.hrsys.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DirectorateRepository directorateRepository;
    private final EmployeeService employeeService;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, DirectorateRepository directorateRepository, EmployeeService employeeService) {
        this.departmentRepository = departmentRepository;
        this.directorateRepository = directorateRepository;
        this.employeeService = employeeService;
    }

    @Transactional
    public Department createDepartment(Department department) {
        if(department == null) {
            throw new InvalidRequestException("Department must not be null");
        }

        // Check if a department with the same name already exists
        if (departmentRepository.findByName(department.getName()) != null) {
            throw new InvalidRequestException("A department with this name already exists.");
        }
        // Fetch the directorate from the database
        Directorate directorate = directorateRepository.findById(department.getDirectorate().getId())
                .orElseThrow(() -> new IllegalArgumentException("Directorate not found"));
        department.setDirectorate(directorate);
        return departmentRepository.save(department);
    }


    // Get all departments
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // Get department by directorate
    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByDirectorate(Long directorateId) {
        return departmentRepository.findByDirectorateId(directorateId);
    }

    @Transactional
    public Department updateDepartment(Department department) {
        if (department == null) {
            throw new InvalidRequestException("Department must not be null");
        }
        // Fetch the existing department from the database
        Department existingDepartment = departmentRepository.findById(department.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", department.getId()));
        // Check if the provided version matches the current version
        if (department.getVersion() == null || existingDepartment.getVersion() == null || !department.getVersion().equals(existingDepartment.getVersion())) {
            throw new InvalidRequestException("The provided version does not match the current version");
        }
        // Update the existing department
        updateExistingDepartment(existingDepartment, department);
        return departmentRepository.save(existingDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id, Long newDepartmentId) {
        if (id == null || newDepartmentId == null) {
            throw new InvalidRequestException("Department id and new department id must not be null");
        }

        if (id.equals(newDepartmentId)) {
            throw new InvalidRequestException("Department id and new department id must not be the same");
        }

        // Fetch the department to be deleted from the database
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
        // Fetch the new department from the database
        Department newDepartment = departmentRepository.findById(newDepartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("New Department", newDepartmentId));
        // Reassign the employees of the old department to the new department
        reassignEmployees(department, newDepartment);

        departmentRepository.delete(department);
    }


    private void updateExistingDepartment(Department existing, Department update) {
        Optional.ofNullable(update.getName()).ifPresent(existing::setName);
        Optional.ofNullable(update.getDirectorate()).ifPresent(directorate -> {
            // Fetch the directorate from the database
            Directorate directorateEntity = directorateRepository.findById(directorate.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Directorate", directorate.getId()));
            existing.setDirectorate(directorateEntity);
        });
    }

    private void reassignEmployees(Department oldDepartment, Department newDepartment) {
        // Fetch the employees of the old department
        List<Employee> employees = employeeService.getEmployeesByDepartment(oldDepartment.getId());
        for (Employee employee : employees) {
            // Reassign the employee to the new department
            employee.setDepartment(newDepartment);
            // Update the employee in the database
            employeeService.updateEmployee(employee);
        }
    }
}
