package org.hrsys.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class EmployeeDTO {

    @NotNull(groups = {OnUpdate.class}, message = "ID is mandatory")
    private Long id;

    @NotBlank(groups = {OnCreate.class}, message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(groups = {OnCreate.class}, message = "Email is mandatory")
    @Email(message = "Email is invalid", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = {OnCreate.class}, message = "Password is mandatory")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters", groups = {OnCreate.class})
    private String password;

    @NotNull(groups = {OnCreate.class}, message = "Role ID is mandatory")
    private Long roleId;

    @NotNull(groups = {OnCreate.class}, message = "Department ID is mandatory")
    private Long departmentId;

    private Long managerId;

    private Long version;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long id, String name, String email, String password, Long roleId, Long departmentId, Long managerId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.departmentId = departmentId;
        this.managerId = managerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
