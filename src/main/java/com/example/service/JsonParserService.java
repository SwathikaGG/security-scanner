package com.example.service;

import com.example.model.SecurityIssue;
import com.example.repository.SecurityIssueRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Service
public class JsonParserService {

    private final SecurityIssueRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonParserService.class);

    public JsonParserService(SecurityIssueRepository repository) {
        this.repository = repository;
    }

    public void parseOwaspReport(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("File not found: {}", filePath);
            throw new IOException("File not found: " + filePath);
        }

        try {
            JsonNode root = mapper.readTree(file);
            JsonNode dependencies = root.get("dependencies");

            if (dependencies != null) {
                for (JsonNode dep : dependencies) {
                    if (dep.has("vulnerabilities")) {
                        for (JsonNode vuln : dep.get("vulnerabilities")) {
                            SecurityIssue issue = new SecurityIssue();
                            issue.setTool("OWASP");
                            issue.setDescription(vuln.get("description").asText());
                            issue.setSeverity(vuln.get("severity").asText());
                            issue.setCveId(vuln.get("name").asText());
                            issue.setFileName(dep.get("fileName").asText());
                            repository.save(issue);
                        }
                    }
                }
            } else {
                logger.warn("No dependencies found in OWASP report");
            }
        } catch (IOException e) {
            logger.error("Error reading OWASP report: {}", e.getMessage());
            throw new IOException("Error reading OWASP report: " + e.getMessage());
        }
    }

    public void parseSafetyReport(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("File not found: {}", filePath);
            throw new IOException("File not found: " + filePath);
        }

        try {
            JsonNode root = mapper.readTree(file);

            for (JsonNode vuln : root) {
                SecurityIssue issue = new SecurityIssue();
                issue.setTool("Safety");
                issue.setDescription(vuln.get("description").asText());
                issue.setSeverity(vuln.get("severity").asText());
                issue.setCveId(vuln.get("vuln_id").asText());
                issue.setFileName(vuln.get("package_name").asText());
                repository.save(issue);
            }
        } catch (IOException e) {
            logger.error("Error reading Safety report: {}", e.getMessage());
            throw new IOException("Error reading Safety report: " + e.getMessage());
        }
    }
}
