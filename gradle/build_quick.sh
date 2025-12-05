#!/bin/bash
# Build without iOS link tasks
# Excludes all iOS linking tasks to speed up builds on non-iOS development

./gradlew build \
  -x :app:ios-kit:linkPodDebugFrameworkIosArm64 \
  -x :app:ios-kit:linkPodReleaseFrameworkIosArm64 \
  -x :app:ios-kit:linkPodDebugFrameworkIosSimulatorArm64 \
  -x :app:ios-kit:linkDebugTestIosSimulatorArm64 \
  -x :app:ios-kit:linkPodReleaseFrameworkIosSimulatorArm64 \
  -x :core:domain:linkDebugTestIosSimulatorArm64 \
  -x :core:presentation:linkDebugTestIosSimulatorArm64 \
  -x :core:test:linkDebugTestIosSimulatorArm64 \
  -x :ui:shared:linkPodDebugFrameworkIosArm64 \
  -x :ui:shared:linkPodReleaseFrameworkIosArm64 \
  -x :ui:shared:linkPodDebugFrameworkIosSimulatorArm64 \
  -x :ui:shared:linkPodReleaseFrameworkIosSimulatorArm64 \
  -x :ui:shared:linkDebugTestIosSimulatorArm64 \
  -x :core:data:db-sqldelight:linkPodDebugFrameworkIosArm64 \
  -x :core:data:db-sqldelight:linkPodDebugFrameworkIosSimulatorArm64 \
  -x :core:data:db-sqldelight:linkPodReleaseFrameworkIosArm64 \
  -x :core:data:db-sqldelight:linkPodReleaseFrameworkIosSimulatorArm64 \
  -x :core:data:db-sqldelight:linkDebugTestIosSimulatorArm64 \
  -x :core:data:file-explorer:linkDebugTestIosSimulatorArm64 \
  -x :thirdparty:androidx:paging:compose:linkDebugTestIosSimulatorArm64 \
  -x :thirdparty:app:cash:sqldelight:paging3:linkDebugTestIosSimulatorArm64

