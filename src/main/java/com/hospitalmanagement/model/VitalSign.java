package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Entity
@Table(name = "vital_signs")
public class VitalSign extends BaseEntity {

    @Column(length = 20)
    private String bloodPressure;

    @DecimalMin("25.0")
    @DecimalMax("45.0")
    private BigDecimal temperatureCelsius;

    @Min(0)
    @Max(250)
    private Integer pulseBpm;

    @Min(0)
    @Max(100)
    private Integer oxygenSaturation;

    @DecimalMin("0.0")
    private BigDecimal weightKg;

    @DecimalMin("0.0")
    private BigDecimal heightCm;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triage_record_id", nullable = false, unique = true)
    private TriageRecord triageRecord;
    public VitalSign() {
    }

    public VitalSign(String bloodPressure, BigDecimal temperatureCelsius, Integer pulseBpm, Integer oxygenSaturation, BigDecimal weightKg, BigDecimal heightCm, TriageRecord triageRecord) {
        this.bloodPressure = bloodPressure;
        this.temperatureCelsius = temperatureCelsius;
        this.pulseBpm = pulseBpm;
        this.oxygenSaturation = oxygenSaturation;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.triageRecord = triageRecord;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public BigDecimal getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(BigDecimal temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public Integer getPulseBpm() {
        return pulseBpm;
    }

    public void setPulseBpm(Integer pulseBpm) {
        this.pulseBpm = pulseBpm;
    }

    public Integer getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(Integer oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public TriageRecord getTriageRecord() {
        return triageRecord;
    }

    public void setTriageRecord(TriageRecord triageRecord) {
        this.triageRecord = triageRecord;
    }

}
