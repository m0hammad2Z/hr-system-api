package org.hrsys.dto;

import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

public class ManagerDTO {
    @NotNull(groups = {OnUpdate.class}, message = "ID is mandatory")
    private Long employeeId;
    private List<EmployeeDTO> employees;


    public ManagerDTO() {
    }

    public ManagerDTO(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }


    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDTO> employees) {
        this.employees = employees;
    }
}
