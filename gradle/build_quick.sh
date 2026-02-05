#!/usr/bin/env bash

set -euo pipefail

# Fast build on non-iOS dev machines.
# This script excludes iOS-related tasks safely:
# 1) For each Gradle module, query the real task list.
# 2) Generate candidates from (taskMiddle × targetSuffix) plus extra no-target tasks.
# 3) Pass only *existing* tasks to Gradle via "-x".

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# The script lives in ./gradle/, but Gradle wrapper is in repo root.
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT_DIR"

GRADLEW="$ROOT_DIR/gradlew"
if [[ ! -x "$GRADLEW" ]]; then
  echo "[build_quick] ERROR: can't find executable Gradle wrapper at '$GRADLEW'" >&2
  exit 2
fi

# -------------------- CONFIG --------------------

MODULE_PREFIXES=(
  ":core:domain:"
  ":core:data:db-room:"
  ":core:data:db-sqldelight:"
  ":feature:file-explorer:data:"
  ":feature:backup:domain:"
  ":feature:backup:ui:"
  ":core:presentation:"
  ":core:test:"
  ":ui:shared:"
  ":ui:test-jvm:"
  ":ui:test:"
  ":thirdparty:app:cash:sqldelight:paging3:"
  ":app:ios-kit:"
)

TARGET_SUFFIXES=(
  "IosArm64"
  "IosSimulatorArm64"
)

# "Task middle" part. Add new items here when new iOS tasks appear.
# Example result task path: :ui:shared:${taskMiddle}${targetSuffix}
TASK_MIDDLES=(
  "cinteropSQLCipher"
  "compileTestKotlin"
  "compileKotlin"
  "link"
  "linkDebugTest"
  "linkPod"
  "linkPodDebugFramework"
  "linkPodReleaseFramework"
)

# Tasks that don't fit (module + middle + target) shape.
TASKS_NO_TARGET=(
  "compileIosMainKotlinMetadata"
  "podInstallSyntheticIos"
  "podBuildSQLCipherIos"
  "podBuildSQLCipherIosSimulator"
  "podSetupBuildSQLCipherIos"
)

# -------------------- IMPLEMENTATION --------------------

# Returns task names for a given module prefix (":a:b:") by calling ":a:b:tasks --all".
# Output: one task name per line.
list_tasks_for_module() {
  local module_prefix="$1"

  # This task exists for any project; if the module path is wrong, Gradle will fail.
  local output
  if ! output=$("$GRADLEW" -q "${module_prefix}tasks" --all 2>/dev/null); then
    return 1
  fi

  # Typical format: "taskName - Description..."
  # We extract the first token before " - ", but only for lines starting with an identifier.
  # This avoids headers like "Tasks runnable from ...".
  awk -F' - ' '/^[[:alnum:]_][^ ]* - /{print $1}' <<<"$output" | sort -u
}

# Checks whether a given task name exists in the provided list (newline-separated).
has_task() {
  local task_list="$1"
  local task_name="$2"
  grep -Fxq "$task_name" <<<"$task_list"
}

# Build exclude args array: ( -x :module:taskName )...
EXCLUDE_ARGS=()
EXCLUDE_TASK_PATHS=()

for module_prefix in "${MODULE_PREFIXES[@]}"; do
  task_list=""
  if ! task_list=$(list_tasks_for_module "$module_prefix"); then
    echo "[build_quick] WARN: can't query tasks for module '$module_prefix' (skipping its excludes)" >&2
    continue
  fi

  # (taskMiddle × targetSuffix)
  for middle in "${TASK_MIDDLES[@]}"; do
    for target in "${TARGET_SUFFIXES[@]}"; do
      task_name="${middle}${target}"
      if has_task "$task_list" "$task_name"; then
        task_path="${module_prefix}${task_name}"
        EXCLUDE_ARGS+=("-x" "$task_path")
        EXCLUDE_TASK_PATHS+=("$task_path")
      fi
    done
  done

  # No-target tasks
  for task_name in "${TASKS_NO_TARGET[@]}"; do
    if has_task "$task_list" "$task_name"; then
      task_path="${module_prefix}${task_name}"
      EXCLUDE_ARGS+=("-x" "$task_path")
      EXCLUDE_TASK_PATHS+=("$task_path")
    fi
  done

done

## Always print excluded tasks for transparency.
echo "[build_quick] Excluding ${#EXCLUDE_TASK_PATHS[@]} tasks:" >&2
if (( ${#EXCLUDE_TASK_PATHS[@]} > 0 )); then
  printf '  %s\n' "${EXCLUDE_TASK_PATHS[@]}" >&2
fi

# Pass all user args through to Gradle, e.g.:
#   ./build_quick.sh --continue --no-daemon
if (( ${#EXCLUDE_ARGS[@]} > 0 )); then
  "$GRADLEW" build "${EXCLUDE_ARGS[@]}" "$@"
else
  "$GRADLEW" build "$@"
fi
