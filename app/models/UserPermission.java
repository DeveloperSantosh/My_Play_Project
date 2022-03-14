package models;

import be.objectify.deadbolt.java.models.Permission;

public class UserPermission implements Permission {
    private int id;
    private String value;

    public UserPermission() {
    }

    public UserPermission(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getValue() {
        return null;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
