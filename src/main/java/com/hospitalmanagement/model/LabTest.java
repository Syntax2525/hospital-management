package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lab_tests")
public class LabTest extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 40)
    private String code;

    @Column(length = 80)
    private String category;

    @DecimalMin("0.00")
    private BigDecimal price;

    @OneToMany(mappedBy = "labTest")
    private List<LabOrderItem> orderItems = new ArrayList<>();
    public LabTest() {
    }

    public LabTest(String name, String code, String category, BigDecimal price, List<LabOrderItem> orderItems) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.price = price;
        this.orderItems = orderItems;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<LabOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<LabOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

}
