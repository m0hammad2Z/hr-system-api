package org.hrsys.service;

import org.hrsys.enums.LeaveStatus;
import org.hrsys.exception.InvalidRequestException;
import org.hrsys.exception.NotAuthoriseException;
import org.hrsys.exception.ResourceNotFoundException;
import org.hrsys.model.Employee;
import org.hrsys.model.Leave;
import org.hrsys.repository.EmployeeRepository;
import org.hrsys.repository.LeaveRepository;
import org.hrsys.repository.ManagerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LeaveService {
    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;

    public LeaveService(LeaveRepository leaveRepository, EmployeeRepository employeeRepository, ManagerRepository managerRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
    }

    //Apply for leave
    @Transactional
    public Leave applyForLeave(LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate); //Check if dates are valid

        // Get the authenticated employee
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Employee employee = Optional.ofNullable(employeeRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("Employee"));

        // Fetch the employee from the database
        employee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee"));

        // Create a new leave object
        Leave leave = new Leave(employee, startDate, endDate);

        // Check if the employee has a manager
        if(employee.getManager() == null) {
            throw new InvalidRequestException("Employee does not have a manager");
        }

        // Fetch the manager from the database
        leave.setManager(managerRepository.findByEmployeeId(employee.getManager().getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager")));
        leave.setStatus(LeaveStatus.PENDING); //Set status to pending
        return leaveRepository.save(leave);
    }


    // Transactional method to approve a leave
    @Transactional
    public Leave approveLeave(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String employeeEmail = authentication.getName(); // Get the authenticated Manager's email

        // Fetch the authenticated employee from the database
        Employee employee = Optional.ofNullable(employeeRepository.findByEmail(employeeEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Employee"));

        // Fetch the leave from the database
        Leave leave = getLeaveById(id);
        // Check if the authenticated employee is authorized to approve the leave
        if (!Objects.equals(leave.getManager().getEmployeeId(), employee.getId())) {
            throw new NotAuthoriseException("Not authorized to approve this leave");
        }

        leave.setStatus(LeaveStatus.APPROVED); // Set the leave status as APPROVED
        return leaveRepository.save(leave);
    }

    // Transactional method to reject a leave
    @Transactional
    public Leave rejectLeave(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String managerEmail = authentication.getName(); // Get the authenticated Manager's email

        // Fetch the manager from the database
        Employee employee = Optional.ofNullable(employeeRepository.findByEmail(managerEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Manager"));

        // Fetch the leave from the database
        Leave leave = getLeaveById(id);
        // Check if the authenticated employee is authorized to reject the leave
        if (!Objects.equals(leave.getManager().getEmployeeId(), employee.getId())) {
            throw new NotAuthoriseException("Not authorized to reject this leave");
        }

        leave.setStatus(LeaveStatus.REJECTED); // Set the leave status as REJECTED
        return leaveRepository.save(leave);
    }

    // Get leaves by date
    @Transactional(readOnly = true)
    public List<Leave> getMyLeavesByDate(LocalDate startDate, LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get the authenticated employee's email
        System.out.println(email);

        // Fetch the authenticated employee from the database
        Employee employee = Optional.ofNullable(employeeRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("Employee"));

        validateDates(startDate, endDate); // Validate the dates
        // Fetch the leaves from the database
        return leaveRepository.findByEmployeeIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(employee.getId(), startDate, endDate);
    }

    // Method to validate dates
    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException("The start date must be before the end date.");
        }
        if (startDate.isAfter(LocalDate.now())) {
            throw new InvalidRequestException("The start date must be in the past.");
        }
    }

    // Method to fetch a leave by ID
    private Leave getLeaveById(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", id));
    }
}