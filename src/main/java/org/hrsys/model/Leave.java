package org.hrsys.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hrsys.enums.LeaveStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;

@Entity
@Table(name = "leaves")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    @NotNull(groups = {OnCreate.class}, message = "Start date is mandatory")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(groups = {OnCreate.class}, message = "End date is mandatory")
    private LocalDate endDate;

    @NotNull(groups = {OnCreate.class}, message = "Employee is mandatory")
    @ManyToOne(fetch = FetchType.LAZY, optional = false , cascade = CascadeType.REMOVE)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnore
    private Employee employee;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    @JsonIgnore
    private Manager manager;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(groups = {OnUpdate.class}, message = "Status is mandatory")
    private LeaveStatus status;

    @Version
    private Long version;

    public Leave() {
    }

    public Leave(Employee employee, LocalDate startDate, LocalDate endDate) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Leave(Employee employee, LocalDate startDate, LocalDate endDate, Manager manager) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.manager = manager;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
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
