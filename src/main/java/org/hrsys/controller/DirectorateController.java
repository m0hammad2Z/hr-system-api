package org.hrsys.controller;

import org.hrsys.dto.DirectorateDTO;
import org.hrsys.model.ApiResponse;
import org.hrsys.model.Directorate;
import org.hrsys.service.DirectorateService;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/directorates")
public class DirectorateController {

    private final DirectorateService directorateService;
    private final ModelMapper modelMapper;

    @Autowired
    public DirectorateController(DirectorateService directorateService, ModelMapper modelMapper) {
        this.directorateService = directorateService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<DirectorateDTO>> createDirectorate(@Validated(OnCreate.class) @RequestBody DirectorateDTO directorateDTO) {
        Directorate directorate = modelMapper.map(directorateDTO, Directorate.class);
        Directorate createdDirectorate = directorateService.createDirectorate(directorate);
        DirectorateDTO createdDirectorateDTO = modelMapper.map(createdDirectorate, DirectorateDTO.class);
        ApiResponse<DirectorateDTO> apiResponse = new ApiResponse<>(true, "Directorate created successfully", createdDirectorateDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DirectorateDTO>>> getAllDirectorates() {
        List<DirectorateDTO> directorates = modelMapper.map(directorateService.getAllDirectorates(), new TypeToken<List<DirectorateDTO>>() {}.getType());
        ApiResponse<List<DirectorateDTO>> apiResponse = new ApiResponse<>(true, "Directorates fetched successfully", directorates);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DirectorateDTO>> getDirectorateById(@PathVariable Long id) {
        DirectorateDTO directorate = modelMapper.map(directorateService.getDirectorateById(id), DirectorateDTO.class);
        ApiResponse<DirectorateDTO> apiResponse = new ApiResponse<>(true, "Directorate fetched successfully", directorate);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping
    public ResponseEntity<ApiResponse<DirectorateDTO>> updateDirectorate(@Validated(OnUpdate.class) @RequestBody DirectorateDTO directorateDTO) {
        Directorate directorate = modelMapper.map(directorateDTO, Directorate.class);
        Directorate updatedDirectorate = directorateService.updateDirectorate(directorate);
        DirectorateDTO updatedDirectorateDTO = modelMapper.map(updatedDirectorate, DirectorateDTO.class);
        ApiResponse<DirectorateDTO> apiResponse = new ApiResponse<>(true, "Directorate updated successfully", updatedDirectorateDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDirectorate(@PathVariable Long id, @RequestParam(required = false) Long newDirectorateId) {
        directorateService.deleteDirectorate(id, newDirectorateId);
        ApiResponse<String> apiResponse = new ApiResponse<>(true, "Directorate deleted successfully", null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
