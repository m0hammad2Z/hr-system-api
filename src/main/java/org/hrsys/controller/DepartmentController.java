package org.hrsys.controller;

import org.hrsys.dto.DepartmentDTO;
import org.hrsys.model.ApiResponse;
import org.hrsys.model.Department;
import org.hrsys.service.DepartmentService;
import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public DepartmentController(DepartmentService departmentService, ModelMapper modelMapper) {
        this.departmentService = departmentService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(@Validated(OnCreate.class) @RequestBody DepartmentDTO departmentDTO) {
        Department department = modelMapper.map(departmentDTO, Department.class);
        Department createdDepartment = departmentService.createDepartment(department);
        DepartmentDTO createdDepartmentDTO = modelMapper.map(createdDepartment, DepartmentDTO.class);
        ApiResponse<DepartmentDTO> apiResponse = new ApiResponse<>(true, "Department created successfully", createdDepartmentDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        List<DepartmentDTO> departments = modelMapper.map(departmentService.getAllDepartments(), new TypeToken<List<DepartmentDTO>>() {}.getType());
        ApiResponse<List<DepartmentDTO>> apiResponse = new ApiResponse<>(true, "Departments fetched successfully", departments);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/getByDirectorate/{directorateId}")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getDepartmentsByDirectorate(@PathVariable Long directorateId) {
        List<DepartmentDTO> departments = modelMapper.map(departmentService.getDepartmentsByDirectorate(directorateId), new TypeToken<List<DepartmentDTO>>() {}.getType());
        ApiResponse<List<DepartmentDTO>> apiResponse = new ApiResponse<>(true, "Departments fetched successfully", departments);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(@Validated(OnUpdate.class) @RequestBody DepartmentDTO departmentDTO) {
        Department department = modelMapper.map(departmentDTO, Department.class);
        Department updatedDepartment = departmentService.updateDepartment(department);
        DepartmentDTO updatedDepartmentDTO = modelMapper.map(updatedDepartment, DepartmentDTO.class);
        ApiResponse<DepartmentDTO> apiResponse = new ApiResponse<>(true, "Department updated successfully", updatedDepartmentDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id, @RequestParam(required = false) Long newDepartmentId) {
        departmentService.deleteDepartment(id, newDepartmentId);
        ApiResponse<String> apiResponse = new ApiResponse<>(true, "Department deleted successfully", null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
