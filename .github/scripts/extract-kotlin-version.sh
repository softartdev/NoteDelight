#!/usr/bin/env bash
set -euo pipefail

# This script extracts the Kotlin version from gradle/libs.versions.toml
# and prints it to stdout.
#
# It is intended to be used from GitHub Actions to build cache keys that
# depend only on the Kotlin version.

# Resolve repository root assuming the script is located at .github/scripts/
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
VERSIONS_FILE="$REPO_ROOT/gradle/libs.versions.toml"

if [[ ! -f "$VERSIONS_FILE" ]]; then
  echo "gradle/libs.versions.toml not found at: $VERSIONS_FILE" >&2
  exit 1
fi

# Find the line like: kotlin = "2.2.21"
LINE="$(grep -E '^kotlin *= *"' "$VERSIONS_FILE" | head -n1 || true)"

if [[ -z "$LINE" ]]; then
  echo 'Could not find a line with kotlin = "..." in gradle/libs.versions.toml' >&2
  exit 1
fi

# Extract the value between quotes
VERSION="$(sed -E 's/.*"([^"]+)".*/\1/' <<< "$LINE")"

if [[ -z "$VERSION" ]]; then
  echo 'Failed to extract Kotlin version from line:' >&2
  echo "$LINE" >&2
  exit 1
fi

echo "$VERSION"