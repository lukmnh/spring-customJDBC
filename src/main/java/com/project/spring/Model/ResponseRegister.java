package com.project.spring.Model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

public class ResponseRegister {

    private String fullname;
    private String email;
    @CreationTimestamp
    private LocalDateTime bod;
    private String address;
    private Long manager_id;
    private String managerName;
    private String password;
    private Role role;
    private String roleName;
    private int roleLevel;

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

    public LocalDateTime getBod() {
        return bod;
    }

    public void setBod(LocalDateTime bod) {
        this.bod = bod;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getManager_id() {
        return manager_id;
    }

    public void setManager_id(Long manager_id) {
        this.manager_id = manager_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
