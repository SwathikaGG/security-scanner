// src/main/java/com/example/model/SecurityIssue.java
package com.example.model;

import jakarta.persistence.*;

@Entity
public class SecurityIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tool; // OWASP/Safety
    private String severity;
    private String description;
    private String fileName;
    private String cveId;

    // Getters and setters

    


    // Getter methods (optional)
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
}

