package models;

import be.objectify.deadbolt.java.models.Role;

public class UserRole implements Role {
    private String roleType;
    private String description;

    public UserRole() {
    }

    public UserRole(String roleType, String description) {
        this.roleType = roleType;
        this.description = description;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return roleType;
    }
}
