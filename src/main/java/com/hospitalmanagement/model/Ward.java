package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wards")
public class Ward extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 40)
    private String floor;

    @OneToMany(mappedBy = "ward")
    private List<Bed> beds = new ArrayList<>();
    public Ward() {
    }

    public Ward(String name, String floor, List<Bed> beds) {
        this.name = name;
        this.floor = floor;
        this.beds = beds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public List<Bed> getBeds() {
        return beds;
    }

    public void setBeds(List<Bed> beds) {
        this.beds = beds;
    }

}
