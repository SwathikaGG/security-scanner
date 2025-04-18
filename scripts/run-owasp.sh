#!/bin/bash

# Set variables
PROJECT_NAME="Security Scanner"
SCAN_PATH="/var/lib/jenkins/workspace/scan"  # Absolute path to your project
OUTPUT_DIR="$SCAN_PATH/output"  # Output directory for the report
DATA_DIR="$SCAN_PATH/dc-data"  # Persistent DB cache directory
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
OUTPUT_FILE="$OUTPUT_DIR/dependency-check-report-$TIMESTAMP.json"
FIXED_NAME_FILE="$OUTPUT_DIR/dependency-check-report.json"  # File name without timestamp

# Dependency-Check CLI location
DEPENDENCY_CHECK_CMD="/opt/dependency-check-12.1.0/dependency-check/bin/dependency-check.sh"

# Ensure output and data directories exist
mkdir -p "$OUTPUT_DIR"
mkdir -p "$DATA_DIR"

# Run Dependency-Check
echo "🛡️ Running OWASP Dependency-Check..."
$DEPENDENCY_CHECK_CMD --version 
$DEPENDENCY_CHECK_CMD \
  --project "$PROJECT_NAME" \
  --scan "$SCAN_PATH" \
  --format JSON \
  --out "$OUTPUT_DIR" \
  --data "$DATA_DIR"

# Debugging: List files in the output directory
echo "Listing output directory files..."
ls -l "$OUTPUT_DIR"  # List all files in the output directory

# Rename the generated JSON report
if [ -f "$FIXED_NAME_FILE" ]; then
    mv "$FIXED_NAME_FILE" "$OUTPUT_FILE"  # Rename to include timestamp
    ln -sf "$OUTPUT_FILE" "$FIXED_NAME_FILE"  # ✅ Create/Update symlink to latest report
    echo "✅ Report saved as: $OUTPUT_FILE"
    echo "🔗 Symlink updated: $FIXED_NAME_FILE → $OUTPUT_FILE"
else
    echo "❌ Error: JSON report not found in $OUTPUT_DIR"
    exit 1
fi
