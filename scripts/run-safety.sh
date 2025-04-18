#!/bin/bash
# OWASP Dependency-Check Script
echo "🔍 Running OWASP Dependency-Check..."

dependency-check --project "Security Scanner" \
    --scan ~/security-scanner \
    --format JSON \
    --out ~/output

echo "✅ OWASP scan completed! Output saved in ~/output"
