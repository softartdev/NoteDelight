#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
PROJECT_NAME="${PROJECT_NAME:-NoteDelight}"
PREVIEW_FILE="core/test/ui/src/androidMain/kotlin/com/softartdev/notedelight/screenshot_preview/ScreenshootPreview.kt"
OUTPUT_ROOT="$REPO_ROOT/docs/screenshoots/store"

SCREENSHOTS=(
  "phone|light|01|notes|StorePhoneLightNotesPreview|1080|1920"
  "phone|light|02|note-detail|StorePhoneLightNoteDetailPreview|1080|1920"
  "phone|light|03|sign-in|StorePhoneLightSignInPreview|1080|1920"
  "phone|light|04|security-settings|StorePhoneLightSecuritySettingsPreview|1080|1920"
  "phone|dark|01|notes|StorePhoneDarkNotesPreview|1080|1920"
  "phone|dark|02|note-detail|StorePhoneDarkNoteDetailPreview|1080|1920"
  "phone|dark|03|sign-in|StorePhoneDarkSignInPreview|1080|1920"
  "phone|dark|04|security-settings|StorePhoneDarkSecuritySettingsPreview|1080|1920"
  "tablet|light|01|notes|StoreTabletLightNotesPreview|1920|1200"
  "tablet|light|02|sign-in|StoreTabletLightSignInPreview|1920|1200"
  "tablet|light|03|security-settings|StoreTabletLightSecuritySettingsPreview|1920|1200"
  "tablet|dark|01|notes|StoreTabletDarkNotesPreview|1920|1200"
  "tablet|dark|02|sign-in|StoreTabletDarkSignInPreview|1920|1200"
  "tablet|dark|03|security-settings|StoreTabletDarkSecuritySettingsPreview|1920|1200"
)

for screenshot in "${SCREENSHOTS[@]}"; do
  IFS="|" read -r form theme number scenario composable expected_width expected_height <<< "$screenshot"
  output_file="$OUTPUT_ROOT/$form/$theme/$number-$scenario.png"
  mkdir -p "$(dirname "$output_file")"

  echo "Rendering $composable -> ${output_file#"$REPO_ROOT"/}"
  android studio render-compose-preview \
    --project="$PROJECT_NAME" \
    "$PREVIEW_FILE" \
    "$composable" \
    --output-image-file="$output_file"

  if [[ ! -s "$output_file" ]]; then
    echo "Screenshot was not created or is empty: $output_file" >&2
    exit 1
  fi

  actual_width="$(sips -g pixelWidth "$output_file" | awk '/pixelWidth/ {print $2}')"
  actual_height="$(sips -g pixelHeight "$output_file" | awk '/pixelHeight/ {print $2}')"
  if [[ "$actual_width" != "$expected_width" || "$actual_height" != "$expected_height" ]]; then
    echo "Unexpected dimensions for $output_file: ${actual_width}x${actual_height}, expected ${expected_width}x${expected_height}" >&2
    exit 1
  fi
done

echo "Generated ${#SCREENSHOTS[@]} screenshots in ${OUTPUT_ROOT#"$REPO_ROOT"/}"
