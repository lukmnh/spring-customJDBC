package com.project.spring.Model;

import java.time.LocalDate;

public class RequestRegister {

    private String fullname;
    private String email;
    private LocalDate bod;
    private String address;
    private int managerId;
    private String password;
    // private User user;
    // private Role role;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBod() {
        return bod;
    }

    public void setBod(LocalDate bod) {
        this.bod = bod;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    // public Role getRole() {
    // return role;
    // }

    // public void setRole(Role role) {
    // this.role = role;
    // }

    // public User getUser() {
    // return user;
    // }

    // public void setUser(User user) {
    // this.user = user;
    // }
}
