package dto;

import models.MyRole;

public class RequestRole {
    private String roleType;
    private String roleDescription;

    public RequestRole() {
    }

    public RequestRole(String roleType, String roleDescription) {
        this.roleType = roleType;
        this.roleDescription = roleDescription;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public MyRole getMyRole(){
        return MyRole.newBuilder()
                .setRoleType(roleType)
                .setDescription(roleDescription)
                .build();
    }

    public String validate(){
        if (roleType.isBlank()) return "Role Type cannot be empty.";
        if (roleType.length()>200)  return "Role Type >200 character.";
        if (roleDescription.isBlank()) return "Role Description cannot be empty.";
        if (roleType.length()>200)  return "Role Description >200 character.";
        return "valid";
    }
}
