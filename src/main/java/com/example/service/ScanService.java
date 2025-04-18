package com.example.service;

import com.example.model.ScanReport;
import com.example.repository.ScanReportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class ScanService {

    @Autowired
    protected ScanReportRepository repo;

    protected ObjectMapper mapper = new ObjectMapper();

    public ScanReport saveReport(ScanReport report) {
        return repo.save(report);
    }

    public List<ScanReport> getAllReports() {
        return repo.findAll();
    }

    public Process runShellScript(String scriptPath) throws IOException {
        return new ProcessBuilder("sh", scriptPath).start();
    }
    
    public List<ScanReport> getReportsByTool(String tool) {
        return repo.findByTool(tool);
    }
    

    // 🆕 Add this method to parse OWASP Dependency-Check report
    public void parseOwaspReport(String filePath) throws Exception {
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode dependencies = root.get("dependencies");

        for (JsonNode dep : dependencies) {
            if (dep.has("vulnerabilities")) {
                for (JsonNode vuln : dep.get("vulnerabilities")) {
                    ScanReport report = new ScanReport();
                    report.setTool("OWASP");
                    report.setDescription(vuln.get("description").asText());
                    report.setSeverity(vuln.get("severity").asText());
                    report.setCveId(vuln.get("name").asText());
                    report.setFileName(dep.get("fileName").asText());
                    repo.save(report);
                }
            }
        }
    }

    // 🆕 Add this method to parse Safety report
    public void parseSafetyReport(String filePath) throws Exception {
        JsonNode root = mapper.readTree(new File(filePath));

        for (JsonNode vuln : root) {
            ScanReport report = new ScanReport();
            report.setTool("Safety");
            report.setDescription(vuln.get("description").asText());
            report.setSeverity(vuln.get("severity").asText());
            report.setCveId(vuln.get("vuln_id").asText());
            report.setFileName(vuln.get("package_name").asText());
            repo.save(report);
        }
    }

    // 🆕 Method to trigger scans and store reports
    public void runScansAndStoreReports() throws Exception {
        // Step 1: Run OWASP scan script
        ProcessBuilder owaspBuilder = new ProcessBuilder("sh", "/home/user/scripts/run-owasp.sh");
        owaspBuilder.inheritIO();
        Process owaspProcess = owaspBuilder.start();
        int owaspExit = owaspProcess.waitFor();
    
        // Handle errors for OWASP scan
        if (owaspExit != 0) {
            throw new RuntimeException("OWASP scan failed with exit code " + owaspExit);
        }
        // Log or report the successful completion of OWASP scan
        System.out.println("OWASP scan completed successfully.");
    
        // Step 2: Run Safety scan script
        ProcessBuilder safetyBuilder = new ProcessBuilder("sh", "/home/user/scripts/run-safety.sh");
        safetyBuilder.inheritIO();
        Process safetyProcess = safetyBuilder.start();
        int safetyExit = safetyProcess.waitFor();
    
        // Handle errors for Safety scan
        if (safetyExit != 0) {
            throw new RuntimeException("Safety scan failed with exit code " + safetyExit);
        }
        // Log or report the successful completion of Safety scan
        System.out.println("Safety scan completed successfully.");
    
        // Step 3: Parse OWASP report and store it in the database
        try {
            parseOwaspReport("/home/user/output/dependency-check-report.json");
            System.out.println("OWASP report parsed successfully.");
        } catch (Exception e) {
            // Log error for OWASP parsing failure
            System.err.println("Error parsing OWASP report: " + e.getMessage());
            throw e;
        }
    
        // Step 4: Parse Safety report and store it in the database
        try {
            parseSafetyReport("/home/user/output/safety-report.json");
            System.out.println("Safety report parsed successfully.");
        } catch (Exception e) {
            // Log error for Safety parsing failure
            System.err.println("Error parsing Safety report: " + e.getMessage());
            throw e;
        }
    }
    
}
