package org.hrsys.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hrsys.enums.LeaveStatus;
import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
public class LeaveDTO {
    @NotNull(groups = {OnUpdate.class}, message = "ID is mandatory")
    private Long id;
    @NotNull(groups = {OnCreate.class}, message = "Start date is mandatory")
    private LocalDate startDate;

    @NotNull(groups = {OnCreate.class}, message = "End date is mandatory")
    private LocalDate endDate;

    @NotNull(groups = {OnCreate.class}, message = "Employee ID is mandatory")
    private Long employeeId;

    @NotNull(groups = {OnUpdate.class}, message = "Status is mandatory")
    private LeaveStatus status;
    private Long version;
    public LeaveDTO() {
    }

    public LeaveDTO(Long id, Long employeeId, LocalDate startDate, LocalDate endDate, LeaveStatus status) {
        this.id = id;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = LeaveStatus.valueOf(String.valueOf(status));
    }

    public LeaveDTO(Long employeeId, LocalDate startDate, LocalDate endDate, LeaveStatus status) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = LeaveStatus.valueOf(String.valueOf(status));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
