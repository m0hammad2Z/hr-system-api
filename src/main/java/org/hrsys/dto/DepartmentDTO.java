package org.hrsys.dto;

import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DepartmentDTO {
    @NotNull(groups = {OnUpdate.class}, message = "ID is mandatory")
    private Long id;
    @NotBlank(groups = {OnCreate.class}, message = "Department name must not be blank")
    @Size(min = 3, max = 50, groups = {OnCreate.class, OnUpdate.class}, message = "Department name must be between 3 and 50 characters")
    private String name;

    @NotNull(groups = {OnCreate.class}, message = "Directorate is mandatory")
    private Long directorateId;

    private Long version;

    public DepartmentDTO() {
    }

    public DepartmentDTO(Long id, String name, Long directorateId) {
        this.id = id;
        this.name = name;
        this.directorateId = directorateId;
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

    public Long getDirectorateId() {
        return directorateId;
    }

    public void setDirectorateId(Long directorateId) {
        this.directorateId = directorateId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
