package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results")
public class LabResult extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 4000)
    private String resultValues;

    @Column(length = 1000)
    private String interpretation;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_item_id", nullable = false, unique = true)
    private LabOrderItem labOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by_id")
    private StaffUser publishedBy;
    public LabResult() {
    }

    public LabResult(String resultValues, String interpretation, LocalDateTime publishedAt, LabOrderItem labOrderItem, StaffUser publishedBy) {
        this.resultValues = resultValues;
        this.interpretation = interpretation;
        this.publishedAt = publishedAt;
        this.labOrderItem = labOrderItem;
        this.publishedBy = publishedBy;
    }

    public String getResultValues() {
        return resultValues;
    }

    public void setResultValues(String resultValues) {
        this.resultValues = resultValues;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LabOrderItem getLabOrderItem() {
        return labOrderItem;
    }

    public void setLabOrderItem(LabOrderItem labOrderItem) {
        this.labOrderItem = labOrderItem;
    }

    public StaffUser getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(StaffUser publishedBy) {
        this.publishedBy = publishedBy;
    }

}
