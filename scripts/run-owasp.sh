#!/bin/bash

# Set variables
PROJECT_NAME="Security Scanner"
SCAN_PATH="$WORKSPACE"  # Absolute path to your project
OUTPUT_DIR="$WORKSPACE/output"  # Output directory for the report
DATA_DIR="$WORKSPACE/dc-data"  # Persistent DB cache directory
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
OUTPUT_FILE="$OUTPUT_DIR/dependency-check-report-$TIMESTAMP.json"

# Ensure output and data directories exist
mkdir -p "$OUTPUT_DIR"
mkdir -p "$DATA_DIR"

# Run Dependency-Check
echo "🛡️ Running OWASP Dependency-Check..."
dependency-check \
  --project "$PROJECT_NAME" \
  --scan "$SCAN_PATH" \
  --format JSON \
  --out "$OUTPUT_DIR" \
  --data "$DATA_DIR"

# Rename the generated JSON report
if [ -f "$OUTPUT_DIR/dependency-check-report.json" ]; then
    mv "$OUTPUT_DIR/dependency-check-report.json" "$OUTPUT_FILE"
    echo "✅ Report saved as: $OUTPUT_FILE"
else
    echo "❌ Error: JSON report not found in $OUTPUT_DIR"
    exit 1
fi
