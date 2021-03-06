package models;

import be.objectify.deadbolt.java.models.Permission;
import be.objectify.deadbolt.java.models.Subject;
import java.util.List;

public class User implements Subject {

    private int id;
    private String username;
    private String password;
    private String email;
    private List<UserRole> roles;
    private List<UserPermission> permissions;

    public User() {
    }

    public User(int id, String username, String password, String email, List<UserRole> roles, List<UserPermission> permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public void setPermissions(List<UserPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<UserPermission> getPermissions() {
        return permissions;
    }

    @Override
    public String getIdentifier() {
        return email;
    }

    @Override
    public List<UserRole> getRoles() {
        return roles;
    }


}

