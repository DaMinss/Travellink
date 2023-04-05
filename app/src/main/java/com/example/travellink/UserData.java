package com.example.travellink;

public class UserData {
    private String id = null;
    private String first_name = null;
    private String last_name = null;
    private String Email = null;
    private String phone = null;
    private String password = null;


    public UserData(String id, String first_name, String last_name, String email, String phone, String password) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        Email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
