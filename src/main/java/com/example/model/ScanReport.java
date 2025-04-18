package com.example.model;

import jakarta.persistence.*;

@Entity
public class ScanReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tool;
    private String description;
    private String severity;
    private String cveId;
    private String fileName;

    // No-arg constructor required by JPA
    public ScanReport() {
    }

    // All-arg constructor
    public ScanReport(String tool, String description, String severity, String cveId, String fileName) {
        this.tool = tool;
        this.description = description;
        this.severity = severity;
        this.cveId = cveId;
        this.fileName = fileName;
    }

    // Getter methods
    public String getTool() {
        return tool;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public String getCveId() {
        return cveId;
    }

    public String getFileName() {
        return fileName;
    }

    // Setter methods
    public void setTool(String tool) {
        this.tool = tool;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setCveId(String cveId) {
        this.cveId = cveId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // toString method
    @Override
    public String toString() {
        return "ScanReport{id=" + id + ", tool='" + tool + "', description='" + description + "', severity='" + severity + "', cveId='" + cveId + "', fileName='" + fileName + "'}";
    }
}
