package com.hospitalmanagement.model;

import com.hospitalmanagement.enums.InventoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medications")
public class Medication extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 140)
    private String name;

    @Column(length = 80)
    private String strength;

    @Column(length = 80)
    private String dosageForm;

    @Min(0)
    private Integer stockQuantity;

    @Min(0)
    private Integer reorderLevel;

    @DecimalMin("0.00")
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryStatus inventoryStatus;

    @OneToMany(mappedBy = "medication")
    private List<PrescriptionItem> prescriptionItems = new ArrayList<>();
    public Medication() {
    }

    public Medication(String name, String strength, String dosageForm, Integer stockQuantity, Integer reorderLevel, BigDecimal unitPrice, InventoryStatus inventoryStatus, List<PrescriptionItem> prescriptionItems) {
        this.name = name;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.unitPrice = unitPrice;
        this.inventoryStatus = inventoryStatus;
        this.prescriptionItems = prescriptionItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public List<PrescriptionItem> getPrescriptionItems() {
        return prescriptionItems;
    }

    public void setPrescriptionItems(List<PrescriptionItem> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }

}
