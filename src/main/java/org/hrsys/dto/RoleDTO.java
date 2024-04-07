package org.hrsys.dto;
import org.hrsys.validation.OnCreate;
import org.hrsys.validation.OnUpdate;



import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RoleDTO {
    private Long roleId;
    @NotBlank(groups = {OnCreate.class}, message = "Role name must not be blank")
    @Size(min = 3, max = 50, groups = {OnCreate.class, OnUpdate.class}, message = "Role name must be between 3 and 50 characters")
    private String name;
    private Long version;

    public RoleDTO() {
    }

    public RoleDTO(Long roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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
}
