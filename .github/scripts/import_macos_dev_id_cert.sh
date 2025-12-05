#!/bin/bash
set -euo pipefail

KEYCHAIN_NAME="build.keychain-db"
KEYCHAIN_PASSWORD=""
KEYCHAIN_PATH="$HOME/Library/Keychains/$KEYCHAIN_NAME"
P12_PATH="./app/desktop/macOS_development.p12"

# Create keychain if it doesn't exist yet
if [ ! -f "$KEYCHAIN_PATH" ]; then
  security create-keychain -p "$KEYCHAIN_PASSWORD" "$KEYCHAIN_NAME"
fi

# Make sure keychain doesn't auto-lock during the build
# -l  : lock after timeout
# -u  : lock when user logs out
# -t  : timeout in seconds (here: 6 hours)
security set-keychain-settings -lut 21600 "$KEYCHAIN_PATH"

# Add keychain to the search list and make it default for this session
security list-keychains -s "$KEYCHAIN_PATH" $(security list-keychains | sed 's/[\",]//g')
security default-keychain -s "$KEYCHAIN_PATH"
security unlock-keychain -p "$KEYCHAIN_PASSWORD" "$KEYCHAIN_PATH"

# Import Developer ID certificate
security import "$P12_PATH" \
  -k "$KEYCHAIN_PATH" \
  -P "$LARGE_SECRET_PASSPHRASE" \
  -A \
  -T /usr/bin/codesign \
  -T /usr/bin/productbuild \
  -T /usr/bin/security

# Allow non-interactive access for codesign / productbuild / notarytool
security set-key-partition-list \
  -S apple-tool:,apple: \
  -s \
  -k "$KEYCHAIN_PASSWORD" \
  "$KEYCHAIN_PATH"