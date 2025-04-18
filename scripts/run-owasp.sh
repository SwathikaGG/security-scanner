#!/bin/bash

# Set variables
PROJECT_NAME="Security Scanner"
SCAN_PATH="/home/user/security-scanner"  # Absolute path to your project
OUTPUT_DIR="/home/user/security-scanner/output"  # Absolute path for the output directory
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
OUTPUT_FILE="$OUTPUT_DIR/dependency-check-report-$TIMESTAMP.json"

# Ensure output directory exists (within the project)
mkdir -p "$OUTPUT_DIR"

# Run Dependency-Check
echo "Running OWASP Dependency-Check..."
dependency-check --project "$PROJECT_NAME" --scan "$SCAN_PATH" --format JSON --out "$OUTPUT_DIR"

# Rename the generated JSON report to include timestamp
if [ -f "$OUTPUT_DIR/dependency-check-report.json" ]; then
    mv "$OUTPUT_DIR/dependency-check-report.json" "$OUTPUT_FILE"
    echo "Report saved as: $OUTPUT_FILE"
else
    echo "❌ Error: JSON report not found in $OUTPUT_DIR"
    exit 1
fi
