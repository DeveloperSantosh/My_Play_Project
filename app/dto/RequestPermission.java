package dto;

import models.MyPermission;

public class RequestPermission {

    private int id;
    private String value;

    public RequestPermission() {
    }

    public RequestPermission(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String validate() {
        if (value.isBlank()) return "Permission value cannot be empty.";
        if (value.length()>200) return "Permission value > 200 character";
        return "valid";
    }

    public MyPermission toMyPermission() {
        return MyPermission.newBuilder()
                .setId(id)
                .setValue(value)
                .build();
    }
}
