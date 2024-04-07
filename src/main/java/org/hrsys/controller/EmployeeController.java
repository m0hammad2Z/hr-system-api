package org.hrsys.controller;

import org.hrsys.dto.EmployeeDTO;
import org.hrsys.model.ApiResponse;
import org.hrsys.model.Employee;
import org.hrsys.service.EmployeeService;
import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(@Validated(OnCreate.class) @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        Employee createdEmployee = employeeService.createEmployee(employee);
        EmployeeDTO createdEmployeeDTO = modelMapper.map(createdEmployee, EmployeeDTO.class);
        System.out.println(createdEmployeeDTO);
        ApiResponse<EmployeeDTO> apiResponse = new ApiResponse<>(true, "Employee created successfully", createdEmployeeDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees() {
        List<EmployeeDTO> employees = modelMapper.map(employeeService.getAllEmployees(), new TypeToken<List<EmployeeDTO>>() {}.getType());
        ApiResponse<List<EmployeeDTO>> apiResponse = new ApiResponse<>(true, "Employees fetched successfully", employees);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = modelMapper.map(employeeService.getEmployeeById(id), EmployeeDTO.class);
        ApiResponse<EmployeeDTO> apiResponse = new ApiResponse<>(true, "Employee fetched successfully", employee);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/creation-date")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getEmployeesByCreationDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<EmployeeDTO> employees = modelMapper.map(employeeService.getEmployeesByCreationDateBetween(from, to), new TypeToken<List<EmployeeDTO>>() {}.getType());
        ApiResponse<List<EmployeeDTO>> apiResponse = new ApiResponse<>(true, "Employees fetched successfully", employees);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(@Validated(OnUpdate.class) @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        EmployeeDTO updatedEmployeeDTO = modelMapper.map(updatedEmployee, EmployeeDTO.class);
        ApiResponse<EmployeeDTO> apiResponse = new ApiResponse<>(true, "Employee updated successfully", updatedEmployeeDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id, @RequestParam(required = false) Long newManagerEmployeeId) {
        employeeService.deleteEmployee(id, newManagerEmployeeId);
        ApiResponse<String> apiResponse = new ApiResponse<>(true, "Employee deleted successfully", null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}