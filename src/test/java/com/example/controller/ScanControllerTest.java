package com.example.controller;

import com.example.model.ScanReport;
import com.example.service.ScanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ScanControllerTest {

    @InjectMocks
    private ScanController scanController;

    @Mock
    private ScanService scanService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(scanController).build();
    }

    @Test
    void testSubmitScan() throws Exception {
        ScanReport report = new ScanReport("OWASP", "Vulnerability description", "High", "CVE-2021-1234", "file1.java");
        when(scanService.saveReport(any(ScanReport.class))).thenReturn(report);

        mockMvc.perform(post("/api/scans/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tool\":\"OWASP\", \"description\":\"Vulnerability description\", \"severity\":\"High\", \"cveId\":\"CVE-2021-1234\", \"fileName\":\"file1.java\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tool").value("OWASP"))
                .andExpect(jsonPath("$.description").value("Vulnerability description"));
    }

    @Test
    void testAllReports() throws Exception {
        ScanReport report1 = new ScanReport("OWASP", "Vulnerability description", "High", "CVE-2021-1234", "file1.java");
        ScanReport report2 = new ScanReport("Safety", "Another vulnerability", "Low", "CVE-2021-5678", "file2.java");

        List<ScanReport> reports = List.of(report1, report2);
        when(scanService.getAllReports()).thenReturn(reports);

        mockMvc.perform(get("/api/scans/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tool").value("OWASP"))
                .andExpect(jsonPath("$[1].tool").value("Safety"));
    }

    @Test
    void testProcessReports() throws Exception {
        mockMvc.perform(post("/api/scans/process"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reports parsed and saved!"));
    }
}
