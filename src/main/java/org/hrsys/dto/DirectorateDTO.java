package org.hrsys.dto;

import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

public class DirectorateDTO {

    @NotNull(groups = {OnUpdate.class}, message = "ID is mandatory")
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Directorate name must not be blank")
    @Size(min = 3, max = 50, groups = {OnCreate.class, OnUpdate.class}, message = "Directorate name must be between 3 and 50 characters")
    private String name;

    private Long version;

    public DirectorateDTO() {
    }

    public DirectorateDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
