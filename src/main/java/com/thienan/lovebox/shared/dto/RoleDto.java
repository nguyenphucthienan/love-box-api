package com.thienan.lovebox.shared.dto;

import com.thienan.lovebox.entity.RoleName;

public class RoleDto {

    private Long id;
    private RoleName name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}
