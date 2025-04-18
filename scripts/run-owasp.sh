#!/bin/bash

# Set variables
PROJECT_NAME="Security Scanner"
SCAN_PATH="/var/lib/jenkins/workspace/scan"  # Absolute path to your project
OUTPUT_DIR="$SCAN_PATH/output"               # Output directory for the report
DATA_DIR="$SCAN_PATH/dc-data"                # Persistent DB cache directory
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
OUTPUT_FILE="$OUTPUT_DIR/dependency-check-report-$TIMESTAMP.json"
FIXED_NAME_FILE="$OUTPUT_DIR/dependency-check-report.json"  # File name without timestamp

# Dependency-Check CLI location
DEPENDENCY_CHECK_CMD="/opt/dependency-check-12.1.0/dependency-check/bin/dependency-check.sh"

# Ensure output and data directories exist
if [ ! -d "$OUTPUT_DIR" ]; then
    echo "📁 Creating output directory: $OUTPUT_DIR"
    mkdir -p "$OUTPUT_DIR"
else
    echo "📁 Output directory already exists: $OUTPUT_DIR"
fi

if [ ! -d "$DATA_DIR" ]; then
    echo "📁 Creating data directory: $DATA_DIR"
    mkdir -p "$DATA_DIR"
else
    echo "📁 Data directory already exists: $DATA_DIR"
fi

# Purge any existing corrupt/incompatible DB
echo "🧹 Purging old/incompatible Dependency-Check database..."
$DEPENDENCY_CHECK_CMD --data "$DATA_DIR" --purge || {
    echo "❌ Failed to purge old DB."
    exit 1
}

# Check if NVD API key is set
if [ -z "$NVD_API_KEY" ]; then
    echo "⚠️  NVD_API_KEY environment variable is not set. Continuing without it (may be slow)."
    API_KEY_OPTION=""
else
    echo "🔑 Using provided NVD API key"
    API_KEY_OPTION="--nvdApiKey $NVD_API_KEY"
fi

# Run Dependency-Check
echo "🛡️ Running OWASP Dependency-Check..."
$DEPENDENCY_CHECK_CMD --version || {
    echo "❌ Dependency-Check failed to run."
    exit 1
}

$DEPENDENCY_CHECK_CMD \
  --project "$PROJECT_NAME" \
  --scan "$SCAN_PATH" \
  --format JSON \
  --out "$OUTPUT_DIR" \
  --data "$DATA_DIR" \
  $API_KEY_OPTION || {
    echo "❌ OWASP Dependency-Check encountered an error."
    exit 1
}

# Debugging: List files in the output directory
echo "📄 Listing output directory files..."
ls -l "$OUTPUT_DIR"

# Find the newly created JSON report
GENERATED_JSON=$(find "$OUTPUT_DIR" -type f -name "dependency-check-report.json")

if [ -f "$GENERATED_JSON" ]; then
    mv "$GENERATED_JSON" "$OUTPUT_FILE"
    ln -sf "$OUTPUT_FILE" "$FIXED_NAME_FILE"
    echo "✅ Report saved as: $OUTPUT_FILE"
    echo "🔗 Symlink updated: $FIXED_NAME_FILE → $OUTPUT_FILE"
else
    echo "❌ Error: JSON report not found in $OUTPUT_DIR"
    exit 1
fi
