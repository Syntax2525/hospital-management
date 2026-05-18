package com.hospitalmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "report_definitions")
public class ReportDefinition extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 80)
    private String module;

    @Column(length = 1000)
    private String description;

    @Column(length = 4000)
    private String queryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private StaffUser createdBy;
    public ReportDefinition() {
    }

    public ReportDefinition(String name, String module, String description, String queryKey, StaffUser createdBy) {
        this.name = name;
        this.module = module;
        this.description = description;
        this.queryKey = queryKey;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public StaffUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StaffUser createdBy) {
        this.createdBy = createdBy;
    }

}
