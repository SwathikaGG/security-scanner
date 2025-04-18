package com.example.service;

import com.example.model.ScanReport;
import com.example.repository.ScanReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ScanServiceTest {

    @InjectMocks
    private ScanService scanService;

    @Mock
    private ScanReportRepository scanReportRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scanService = new ScanService();
        scanService = spy(scanService);
        scanService.repo = scanReportRepository;
    }

    @Test
    void testSaveReport() {
        ScanReport report = new ScanReport("OWASP", "Vulnerability description", "High", "CVE-2021-1234", "file1.java");
        when(scanReportRepository.save(any(ScanReport.class))).thenReturn(report);

        ScanReport savedReport = scanService.saveReport(report);
        assertNotNull(savedReport);
        assertEquals("OWASP", savedReport.getTool());
    }

    @Test
    void testGetAllReports() {
        ScanReport report1 = new ScanReport("OWASP", "Description", "High", "CVE-2021-1234", "file1.java");
        ScanReport report2 = new ScanReport("Safety", "Another vuln", "Low", "CVE-2021-5678", "file2.java");

        List<ScanReport> reports = List.of(report1, report2);
        when(scanReportRepository.findAll()).thenReturn(reports);

        List<ScanReport> allReports = scanService.getAllReports();
        assertEquals(2, allReports.size());
    }

    @Test
    void testParseOwaspReport() throws Exception {
        String json = """
        {
          "dependencies": [
            {
              "fileName": "example.jar",
              "vulnerabilities": [
                {
                  "description": "Test OWASP vuln",
                  "severity": "High",
                  "name": "CVE-TEST-0001"
                }
              ]
            }
          ]
        }
        """;

        File tempFile = File.createTempFile("owasp-report", ".json");
        Files.writeString(tempFile.toPath(), json);

        scanService.mapper = new ObjectMapper();
        scanService.parseOwaspReport(tempFile.getAbsolutePath());

        verify(scanReportRepository, times(1)).save(any(ScanReport.class));
    }

    @Test
    void testParseSafetyReport() throws Exception {
        String json = """
        [
          {
            "description": "Test Safety vuln",
            "severity": "Low",
            "vuln_id": "CVE-SAFE-0001",
            "package_name": "testpkg"
          }
        ]
        """;

        File tempFile = File.createTempFile("safety-report", ".json");
        Files.writeString(tempFile.toPath(), json);

        scanService.mapper = new ObjectMapper();
        scanService.parseSafetyReport(tempFile.getAbsolutePath());

        verify(scanReportRepository, times(1)).save(any(ScanReport.class));
    }

    @Test
    void testRunScansAndStoreReports() throws Exception {
        doNothing().when(scanService).parseOwaspReport(anyString());
        doNothing().when(scanService).parseSafetyReport(anyString());

        // Mock shell execution
        Process mockProcess = mock(Process.class);
        when(mockProcess.waitFor()).thenReturn(0);

        // Inject custom shell script method for testing
        doReturn(mockProcess).when(scanService).runShellScript(any());

        // Add helper to ScanService (see below)
        scanService.runScansAndStoreReports();

        verify(scanService, times(1)).parseOwaspReport(anyString());
        verify(scanService, times(1)).parseSafetyReport(anyString());
    }
}
