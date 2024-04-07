package org.hrsys.controller;

import org.hrsys.dto.EmployeeDTO;
import org.hrsys.dto.ManagerDTO;
import org.hrsys.model.Manager;
import org.hrsys.model.ApiResponse;
import org.hrsys.service.ManagerService;
import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    private final ManagerService managerService;
    private final ModelMapper modelMapper;

    @Autowired
    public ManagerController(ManagerService managerService, ModelMapper modelMapper) {
        this.managerService = managerService;
        this.modelMapper = modelMapper;
    }


    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ManagerDTO>>> getAllManagers() {
        List<ManagerDTO> managers = modelMapper.map(managerService.getAllManagers(), new org.modelmapper.TypeToken<List<ManagerDTO>>() {}.getType());
        ApiResponse<List<ManagerDTO>> apiResponse = new ApiResponse<>(true, "Managers fetched successfully", managers);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManagerDTO>> getManagerById(@PathVariable Long id) {
        Manager manager = managerService.getManagerById(id);
        ManagerDTO managerDTO = modelMapper.map(manager, ManagerDTO.class);
        ApiResponse<ManagerDTO> apiResponse = new ApiResponse<>(true, "Manager fetched successfully", managerDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("upgradeToManager/{id}")
    public ResponseEntity<ApiResponse<ManagerDTO>> upgradeToManager(@PathVariable Long id) {
        Manager manager = managerService.upgradeToManager(id);
        ManagerDTO managerDTO = modelMapper.map(manager, ManagerDTO.class);
        ApiResponse<ManagerDTO> apiResponse = new ApiResponse<>(true, "Manager upgraded successfully", managerDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}