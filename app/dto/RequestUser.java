package dto;

import models.MyPermission;
import models.MyRole;
import models.MyUser;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;

public class RequestUser {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String username;
    @NotNull
    private List<MyRole> roles;
    @NotNull
    private List<MyPermission> permissions;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<MyRole> getRoles() { return roles; }

    public void setRoles(List<MyRole> roles) { this.roles = roles; }

    public void addRole(MyRole role) { roles.add(role);}

    public List<MyPermission> getPermissions() { return permissions; }

    public void setPermissions(List<MyPermission> permissions) { this.permissions = permissions; }

    public String validate(){
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(regex);

        if(email==null || email.trim().isEmpty())
           return "Email Cannot be Empty.";
        else if(!pattern.matcher(email).matches())
            return "Enter valid Email";
        else if(password==null || password.trim().isEmpty())
            return "Password cannot be empty";
        else
            return "valid";
    }

    public MyUser getMyUser(){
        return MyUser.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .setUsername(username)
                .addAllRole(roles)
                .addAllPermission(permissions)
                .build();
    }
}
