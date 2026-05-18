package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "department")
    private List<StaffUser> staffUsers = new ArrayList<>();
    public Department() {
    }

    public Department(String name, String code, String description, List<StaffUser> staffUsers) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.staffUsers = staffUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StaffUser> getStaffUsers() {
        return staffUsers;
    }

    public void setStaffUsers(List<StaffUser> staffUsers) {
        this.staffUsers = staffUsers;
    }

}
