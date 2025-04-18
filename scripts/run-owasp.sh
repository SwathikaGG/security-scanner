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
if [ -f "$FIXED_NAME_FILE" ]; then
    mv "$FIXED_NAME_FILE" "$OUTPUT_FILE"
    ln -sf "$OUTPUT_FILE" "$FIXED_NAME_FILE"  # Create or update the symlink
    echo "✅ Report saved as: $OUTPUT_FILE"
    echo "🔗 Symlinked as: $FIXED_NAME_FILE"
else
    echo "❌ Error: JSON report not found in $OUTPUT_DIR"
    exit 1
fi
