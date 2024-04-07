package org.hrsys.controller;

import org.hrsys.dto.LeaveDTO;
import org.hrsys.model.ApiResponse;
import org.hrsys.model.Leave;
import org.hrsys.service.LeaveService;
import org.hrsys.validation.OnCreate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;
    private final ModelMapper modelMapper;

    @Autowired
    public LeaveController(LeaveService leaveService, ModelMapper modelMapper) {
        this.leaveService = leaveService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LeaveDTO>> applyForLeave(@Validated(OnCreate.class) @RequestBody LeaveDTO leave) {
        Leave leaveToApply = modelMapper.map(leave, Leave.class);
        Leave createdLeave = leaveService.applyForLeave(leaveToApply.getEmployee(), leave.getStartDate(), leave.getEndDate());
        LeaveDTO leaveDTO = modelMapper.map(createdLeave, LeaveDTO.class);
        ApiResponse<LeaveDTO> apiResponse = new ApiResponse<>(true, "Leave applied successfully", leaveDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<LeaveDTO>> approveLeave(@PathVariable Long id) {
        Leave approvedLeave = leaveService.approveLeave(id);
        LeaveDTO leaveDTO = modelMapper.map(approvedLeave, LeaveDTO.class);
        ApiResponse<LeaveDTO> apiResponse = new ApiResponse<>(true, "Leave approved successfully", leaveDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveDTO>> rejectLeave(@PathVariable Long id) {
        Leave rejectedLeave = leaveService.rejectLeave(id);
        LeaveDTO leaveDTO = modelMapper.map(rejectedLeave, LeaveDTO.class);
        ApiResponse<LeaveDTO> apiResponse = new ApiResponse<>(true, "Leave rejected successfully", leaveDTO);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<List<LeaveDTO>>> getMyLeavesByDate(@PathVariable Long employeeId,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Leave> leaves = leaveService.getMyLeavesByDate(employeeId, startDate, endDate);
        List<LeaveDTO> leaveDTOs = modelMapper.map(leaves, new TypeToken<List<LeaveDTO>>() {}.getType());
        ApiResponse<List<LeaveDTO>> apiResponse = new ApiResponse<>(true, "Leaves fetched successfully", leaveDTOs);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
