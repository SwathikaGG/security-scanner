package com.example.repository;

import com.example.model.SecurityIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityIssueRepository extends JpaRepository<SecurityIssue, Long> {
}
