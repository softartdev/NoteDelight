#!/usr/bin/env bash

set -euo pipefail

ipa_path="${1:-app/iosApp/iosApp.ipa}"
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
required_symbols_file="$script_dir/../../app/iosApp/iosApp/CrashlyticsDynamicSymbols.txt"

if [[ ! -f "$ipa_path" ]]; then
    echo "error: IPA not found: $ipa_path" >&2
    exit 1
fi

if [[ ! -f "$required_symbols_file" ]]; then
    echo "error: Required symbols file not found: $required_symbols_file" >&2
    exit 1
fi

verification_dir="$(mktemp -d "${TMPDIR:-/tmp}/notedelight-ios-symbols.XXXXXX")"
trap 'rm -rf -- "$verification_dir"' EXIT

unzip -q "$ipa_path" -d "$verification_dir"

payload_dir="$verification_dir/Payload"
if [[ ! -d "$payload_dir" ]]; then
    echo "error: Payload directory not found in IPA: $ipa_path" >&2
    exit 1
fi

app_bundle="$(find "$payload_dir" -maxdepth 1 -type d -name '*.app' -print -quit)"
if [[ -z "$app_bundle" ]]; then
    echo "error: App bundle not found in IPA: $ipa_path" >&2
    exit 1
fi

executable_name="$(/usr/libexec/PlistBuddy -c 'Print :CFBundleExecutable' "$app_bundle/Info.plist")"
app_executable="$app_bundle/$executable_name"
if [[ ! -f "$app_executable" ]]; then
    echo "error: App executable not found: $app_executable" >&2
    exit 1
fi

exported_symbols="$verification_dir/exported-symbols.txt"
xcrun dyld_info -exports "$app_executable" |
    awk 'NF == 2 && $1 ~ /^0x/ { print $2 }' > "$exported_symbols"

required_symbols=()
while IFS= read -r symbol; do
    if [[ -n "$symbol" ]]; then
        required_symbols+=("$symbol")
    fi
done < "$required_symbols_file"

if ((${#required_symbols[@]} == 0)); then
    echo "error: Required symbols file is empty: $required_symbols_file" >&2
    exit 1
fi

missing_symbols=0
for symbol in "${required_symbols[@]}"; do
    if ! grep -Fqx "$symbol" "$exported_symbols"; then
        echo "error: Required Crashlytics symbol is not exported: $symbol" >&2
        missing_symbols=1
    fi
done

if ((missing_symbols != 0)); then
    exit 1
fi

echo "Verified Crashlytics symbols in $ipa_path"
