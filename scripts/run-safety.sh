#!/bin/bash

# Set variables
PROJECT_NAME="Security Scanner"
SCAN_PATH="/home/user/security-scanner"  # Absolute path to your project
OUTPUT_DIR="/home/user/security-scanner/output"  # Absolute path for the output directory
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
OUTPUT_FILE="$OUTPUT_DIR/safety-report-$TIMESTAMP.json"

# Ensure output directory exists (within the project)
mkdir -p "$OUTPUT_DIR"

# Run Safety
echo "Running Safety..."
safety check --full-report --json --output "$OUTPUT_FILE" "$SCAN_PATH"

# Check if the report was generated
if [ -f "$OUTPUT_FILE" ]; then
    echo "Safety report saved as: $OUTPUT_FILE"
else
    echo "❌ Error: Safety report not found in $OUTPUT_DIR"
    exit 1
fi
