package com.example.controller;

import com.example.model.ScanReport;
import com.example.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
public class ScanController {

    @Autowired
    private ScanService scanService;

    @PostMapping("/submit")
    public ScanReport submitScan(@RequestBody ScanReport report) {
        return scanService.saveReport(report);
    }

    @GetMapping("/all")
    public List<ScanReport> allReports() {
        return scanService.getAllReports();
    }

    @PostMapping("/process")
    public String processReports() {
        try {
            // Call runScansAndStoreReports() to run the scans and store results
            scanService.runScansAndStoreReports();
            return "Scans triggered and results saved!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/reports/{tool}")
public List<ScanReport> getReportsByTool(@PathVariable String tool) {
    if (tool == null || tool.isEmpty()) {
        throw new IllegalArgumentException("Tool parameter is required.");
    }

    if (!tool.equals("OWASP") && !tool.equals("Safety")) {
        throw new IllegalArgumentException("Invalid tool specified.");
    }

    return scanService.getReportsByTool(tool);
}

}
