package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.ScanReport;
import java.util.List; 

public interface ScanReportRepository extends JpaRepository<ScanReport, Long> {
     List<ScanReport> findByTool(String tool);

}
